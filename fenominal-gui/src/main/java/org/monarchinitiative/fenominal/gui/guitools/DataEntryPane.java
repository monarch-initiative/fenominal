package org.monarchinitiative.fenominal.gui.guitools;


import javafx.beans.binding.BooleanBinding;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.function.UnaryOperator;

public class DataEntryPane {


    private String identifier;
    private Integer years;
    private Integer months;
    private boolean valid;



    private GridPane createDataEntryPane() {
        // Instantiate a new Grid Pane
        GridPane gridPane = new GridPane();

        // Position the pane at the center of the screen, both vertically and horizontally
        gridPane.setAlignment(Pos.CENTER);

        // Set a padding of 20px on each side
        gridPane.setPadding(new Insets(40, 40, 40, 40));

        // Set the horizontal gap between columns
        gridPane.setHgap(10);

        // Set the vertical gap between rows
        gridPane.setVgap(10);

        // Add Column Constraints

        // columnOneConstraints will be applied to all the nodes placed in column one.
        ColumnConstraints columnOneConstraints = new ColumnConstraints(100, 100, Double.MAX_VALUE);
        columnOneConstraints.setHalignment(HPos.RIGHT);

        // columnTwoConstraints will be applied to all the nodes placed in column two.
        ColumnConstraints columnTwoConstrains = new ColumnConstraints(200, 200, Double.MAX_VALUE);
        columnTwoConstrains.setHgrow(Priority.ALWAYS);

        gridPane.getColumnConstraints().addAll(columnOneConstraints, columnTwoConstrains);

        return gridPane;
    }

    private void addUIControls(GridPane gridPane) {
        // Add Header
        Label headerLabel = new Label("Data about proband");
        headerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        gridPane.add(headerLabel, 0,0,2,1);
        GridPane.setHalignment(headerLabel, HPos.CENTER);
        GridPane.setMargin(headerLabel, new Insets(20, 0,20,0));

        Label nameLabel = new Label("ID: ");
        gridPane.add(nameLabel, 0,1);
        TextField idField = new TextField();
        idField.setPrefHeight(40);
        gridPane.add(idField, 1,1);


        // Add Email Label
        Label ageLabel = new Label("Age in years: ");
        gridPane.add(ageLabel, 0, 2);

        // Add Email Text Field
        TextField ageField = new TextField();
        ageField.setPrefHeight(40);
        UnaryOperator<TextFormatter.Change> numberValidationFormatter = change -> {
            if(change.getText().matches("\\d{1,3}")){
                return change; //if change is a number
            } else {
                change.setText(""); //else make no change
                change.setRange(    //don't remove any selected text either.
                        change.getRangeStart(),
                        change.getRangeStart()
                );
                return change;
            }
        };
        ageField.setTextFormatter(new TextFormatter<String>(numberValidationFormatter));
        gridPane.add(ageField, 1, 2);


        ChoiceBox<Integer> choiceBox = new ChoiceBox<>();
        for (int i=0;i<12;++i) {
            choiceBox.getItems().add(i);
        }
        choiceBox.setValue(0);

        choiceBox.setOnAction((event) -> {
            int selectedIndex = choiceBox.getSelectionModel().getSelectedIndex();
            this.months = choiceBox.getSelectionModel().getSelectedItem();
        });

        Label monthsLabel = new Label("Months: ");
        gridPane.add(monthsLabel, 0, 3);
        gridPane.add(choiceBox, 1, 3);

        BooleanBinding bb = new BooleanBinding() {
            {
                super.bind(idField.textProperty(),
                        ageField.textProperty());
            }

            @Override
            protected boolean computeValue() {
                return (idField.getText().isEmpty() || ageField.getText().isEmpty());
            }
        };

        // Add Submit Button
        Button submitButton = new Button("Submit");
        submitButton.setPrefHeight(40);
        submitButton.setDefaultButton(true);
        submitButton.setPrefWidth(100);
        submitButton.disableProperty().bind(bb);
        gridPane.add(submitButton, 0, 4, 2, 1);
        GridPane.setHalignment(submitButton, HPos.CENTER);
        GridPane.setMargin(submitButton, new Insets(20, 0,20,0));
        submitButton.setOnAction((e) ->{
            identifier = idField.getText().trim();
            years = Integer.parseInt(ageField.getText());
            months = choiceBox.getSelectionModel().getSelectedItem();
            valid = true;
            Node  source = (Node)  e.getSource();
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

        gridPane.add(cancelButton, 1, 4, 2, 1);
    }


    public GridPane getPane() {
        GridPane gp = createDataEntryPane();
        addUIControls(gp);
        return gp;
    }

    public String getIdentifier() {
        return identifier;
    }

    public Integer getYears() {
        return years;
    }

    public Integer getMonths() {
        return months;
    }

    public boolean isValid() {
        return valid;
    }
}