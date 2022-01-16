package org.monarchinitiative.fenominal.gui.guitools;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.monarchinitiative.fenominal.gui.model.CohortPublicationData;

import java.util.Optional;


public class CohortDataPane {

    public Optional<CohortPublicationData> show() {
        Dialog<CohortPublicationData> dialog = new Dialog<>();
        dialog.setTitle("Information about Publication");
        dialog.setHeaderText("Please enter information about the publication to be curated");

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setMinWidth(200);
        dialogPane.setMinHeight(300);
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        Text text1 = new Text("Enter data about a cohort described in a publication\n");
        text1.setFont(Font.font("Verdana", FontWeight.BOLD,18));
        Text text2 = new Text("This mode should be used for a publication that describes " +
                "multiple patients, whose phenotypic attributes can be entered one at a time.");
        text2.setFont(Font.font("Verdana",FontWeight.NORMAL, 12));
        TextFlow tflow = new TextFlow(text1, text2);
        GridPane gridPane = new GridPane();
        // Position at  center, both vertically and horizontally
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setPadding(new Insets(40, 40, 40, 40));
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        ColumnConstraints columnOneConstraints = new ColumnConstraints(100, 100, Double.MAX_VALUE);
        columnOneConstraints.setHalignment(HPos.RIGHT);
        ColumnConstraints columnTwoConstrains = new ColumnConstraints(200, 200, Double.MAX_VALUE);
        columnTwoConstrains.setHgrow(Priority.ALWAYS);
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
        Label diseaseNameLabel = new Label("Disease name: ");
        gridPane.add(diseaseNameLabel, 0, 3);
        TextField diseaseNameField = new TextField();
        diseaseNameField.setPrefHeight(40);
        diseaseNameField.setPrefWidth(200);
        gridPane.add(diseaseNameField, 1, 3);

        Separator hseparator = new Separator(Orientation.HORIZONTAL);
        hseparator.setStyle("""
                -fx-padding: 5px;
                    -fx-border-insets: 5px;
                    -fx-background-insets: 5px;""");
        VBox allElementsBox = new VBox();
        allElementsBox.getChildren().addAll(tflow,hseparator, gridPane);
        dialogPane.setContent(allElementsBox);
        dialog.setResultConverter((ButtonType button) -> {
            if (button == ButtonType.OK) {
                String pmid = pmidField.getText().strip();
                String omimId = omimIdField.getText().strip();
                String diseaseName = diseaseNameField.getText().strip();

                return new CohortPublicationData(pmid, omimId, diseaseName);
            }
            return null;
        });

        return dialog.showAndWait();
    }







}
