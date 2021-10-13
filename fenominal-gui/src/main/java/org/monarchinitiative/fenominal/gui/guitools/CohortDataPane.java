package org.monarchinitiative.fenominal.gui.guitools;

import javafx.beans.binding.BooleanBinding;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import static javafx.stage.StageStyle.DECORATED;

public class CohortDataPane {

    private String pmid;
    private String omimId;
    private String diseaseName;
    private boolean valid;



    public void showDataEntryPane() {
        GridPane gridPane = new GridPane();
        addUIControls(gridPane);
        // Position at  center, both vertically and horizontally
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setPadding(new Insets(40, 40, 40, 40));
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        ColumnConstraints columnOneConstraints = new ColumnConstraints(100, 100, Double.MAX_VALUE);
        columnOneConstraints.setHalignment(HPos.RIGHT);
        ColumnConstraints columnTwoConstrains = new ColumnConstraints(200, 200, Double.MAX_VALUE);
        columnTwoConstrains.setHgrow(Priority.ALWAYS);
        //gridPane.getColumnConstraints().addAll(columnOneConstraints, columnTwoConstrains);
        Group root = new Group();
        root.getChildren().add(gridPane);
        Scene scene = new Scene(root,600,300, Color.web("#fbfbf2"));
        Stage stage = new Stage(DECORATED);
        stage.setScene(scene);
        stage.showAndWait();
    }

    private void addUIControls(GridPane gridPane) {
        Label headerLabel = new Label("Data about cohort");
        headerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        gridPane.add(headerLabel, 0,0,2,1);
        GridPane.setHalignment(headerLabel, HPos.CENTER);
        GridPane.setMargin(headerLabel, new Insets(20, 0,20,0));
        Label pmidLabel = new Label("PMID: ");
        gridPane.add(pmidLabel, 0,1);
        TextField pmidField = new TextField();
        pmidField.setPrefHeight(40);
        pmidField.setPrefWidth(200);
        gridPane.add(pmidField, 1,1);
        Label diseaseLabel = new Label("Disease OMIM ID: ");
        gridPane.add(diseaseLabel, 0, 2);
        TextField omimIdField = new TextField();
        omimIdField.setPrefHeight(40);
        omimIdField.setPrefWidth(200);
        gridPane.add(omimIdField, 1, 2);
        Label diseaseName = new Label("Disease name: ");
        gridPane.add(diseaseName, 0, 3);
        TextField diseaseNameField = new TextField();
        diseaseNameField.setPrefHeight(40);
        diseaseNameField.setPrefWidth(200);
        gridPane.add(diseaseNameField, 1, 3);
        BooleanBinding bb = new BooleanBinding() {
            {
                super.bind(pmidField.textProperty(),
                        omimIdField.textProperty(),
                        diseaseNameField.textProperty());
            }

            @Override
            protected boolean computeValue() {
                return (pmidField.getText().isEmpty() || omimIdField.getText().isEmpty() || diseaseNameField.getText().isEmpty());
            }
        };

        // Add Submit Button
        Button submitButton = new Button("Submit");
        submitButton.setPrefHeight(40);
        submitButton.setDefaultButton(true);
        submitButton.setPrefWidth(100);
        submitButton.disableProperty().bind(bb);
        gridPane.add(submitButton, 0, 4);
        GridPane.setHalignment(submitButton, HPos.CENTER);
        GridPane.setMargin(submitButton, new Insets(20, 0,20,0));
        submitButton.setOnAction((e) -> {
            setValues(pmidField.getText().replaceAll(" ", ""),
                    omimIdField.getText().replaceAll(" ", ""),
                    diseaseNameField.getText().strip());
            valid = true;
            Node source = (Node)  e.getSource();
            Stage stage  = (Stage) source.getScene().getWindow();
            stage.close();
            e.consume();
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.setPrefHeight(40);
        cancelButton.setDefaultButton(true);
        cancelButton.setPrefWidth(100);
        cancelButton.setCancelButton(true);
        cancelButton.setOnAction((e)->{
            Node  source = (Node)  e.getSource();
            Stage stage  = (Stage) source.getScene().getWindow();
            stage.close();
        });
        gridPane.add(cancelButton, 1, 4);
    }

    private void setValues(String pm, String id, String label) {
        this.pmid = pm;
        this.omimId = id;
        this.diseaseName = label;
    }


    public String getPmid() {
        return pmid;
    }

    public String getOmimId() {
        return omimId;
    }

    public String getDiseaseName() {
        return diseaseName;
    }

    public boolean isValid() {
        return valid;
    }

}
