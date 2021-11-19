package org.monarchinitiative.fenominal.gui.guitools;


import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import static javafx.stage.StageStyle.DECORATED;

public class CaseDataEntryPane {

    private final Browser browser;

    private String isoAge = "";

    private String caseId = "n/a";

    private final static String buttonStyle =
            " -fx-background-color:" +
                    "        linear-gradient(#f2f2f2, #d6d6d6)," +
                    "        linear-gradient(#fcfcfc 0%, #d9d9d9 20%, #d6d6d6 100%)," +
                    "        linear-gradient(#dddddd 0%, #f6f6f6 50%);" +
                    "    -fx-background-radius: 8,7,6;" +
                    "    -fx-background-insets: 0,1,2;" +
                    "    -fx-text-fill: black;\n" +
                    "    -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );";

    public CaseDataEntryPane() {
        browser = new Browser(setupHtml);
    }


    public String getIsoAge() {
        return isoAge;
    }

    public String getCaseId() {
        return caseId;
    }

    public void showAgePickerDialog() {
        Label selection = new Label("Select age:");
        Font font = Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 12);
        selection.setFont(font);
        Spinner<Integer> years = new Spinner<>(0, 110, 0);
        years.setEditable(true);
        years.setPrefSize(75, 25);
        Spinner<Integer> months = new Spinner<>(0, 11, 0);
        months.setEditable(true);
        months.setPrefSize(75, 25);
        Label mLab = new Label("Months: ");
        Spinner<Integer> days = new Spinner<>(0, 30, 0);
        days.setEditable(true);
        days.setPrefSize(75, 25);
        Label dLab = new Label("Days: ");

        Label yLab = new Label("Years: ");
        HBox hbox = new HBox(5);
        hbox.setPadding(new Insets(10, 10, 10, 25));
        hbox.getChildren().addAll(yLab, years, mLab, months, dLab, days);
        HBox pickerBox = new HBox(selection, hbox);
        Button closeButton = new Button("Done");
        closeButton.setOnAction(actionEvent -> ((Stage)(((Button)actionEvent.getSource()).getScene().getWindow())).close());

        closeButton.setStyle(buttonStyle);
        final Separator separator = new Separator();
        separator.setMaxWidth(40);
        HBox buttonBox = new HBox();
        buttonBox.getChildren().addAll(closeButton);

        Label caseIdLabel = new Label("Case ID");
        TextField caseIdTextField = new TextField();
        caseIdTextField.setPrefHeight(40);
        caseIdTextField.setPrefWidth(100);
        HBox caseIdBox = new HBox();
        caseIdBox.getChildren().addAll(caseIdLabel, caseIdTextField);
        VBox root = new VBox();
        root.getChildren().addAll(browser, pickerBox, caseIdBox, buttonBox);
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");
        Scene scene = new Scene(root,750,500, Color.web("#666970"));
        Stage stage = new Stage(DECORATED);
        stage.setScene(scene);
        stage.showAndWait();
        int Y = years.getValue();
        int M = months.getValue();
        int D = days.getValue();
        this.isoAge = "P" + Y + "Y" + M + "M" + D + "D";
        this.caseId = caseIdTextField.getText().trim();
    }



    private final static String setupHtml ="<html><body><h3>Fenomimal Phenopacket generator</h3>" +
            "<p><i>Fenominal</i> allows users to indicate the age of patients by having users indicate the birthdate as" +
            " well as the dates of the medical encounters that are being recorded.<p>" +
            "<p>This dialog is used for a simple case with only one-time point for TSSV output.</p>" +
            "</body></html>";

}