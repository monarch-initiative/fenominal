package org.monarchinitiative.fenominal.gui.guitools;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

import static javafx.stage.StageStyle.DECORATED;


/**
 * TODO Spinner
 * Spinner
 * https://www.tutorialspoint.com/how-to-create-a-spinner-in-javafx
 */
public class AgePickerDialog {

    private final String message;

    private final Browser browser;

    private final List<String> ages;

    private final static String buttonStyle =
            " -fx-background-color:" +
                    "        linear-gradient(#f2f2f2, #d6d6d6)," +
                    "        linear-gradient(#fcfcfc 0%, #d9d9d9 20%, #d6d6d6 100%)," +
                    "        linear-gradient(#dddddd 0%, #f6f6f6 50%);" +
                    "    -fx-background-radius: 8,7,6;" +
                    "    -fx-background-insets: 0,1,2;" +
                    "    -fx-text-fill: black;\n" +
                    "    -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );";

    public AgePickerDialog(String msg) {
        message = msg;
        ages = new ArrayList<>();
        browser = new Browser(message);
    }

    public AgePickerDialog( List<String> previousAges) {
        message = getHtmlWithAges(previousAges);
        browser = new Browser(message);
        ages = new ArrayList<>(previousAges);
    }

    public String showAgePickerDialog() {
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
        closeButton.setOnAction((actionEvent -> ((Stage)(((Button)actionEvent.getSource()).getScene().getWindow())).close()));
        Button clearLatestButton = new Button("Clear last age");
        clearLatestButton.setOnAction((actionEvent -> {
            int i = this.ages.size() - 1;
            if (i>0) {
                this.ages.remove(i);
            }
            years.getValueFactory().setValue(0);
            months.getValueFactory().setValue(0);
            days.getValueFactory().setValue(0);
            String html = getHtmlWithAges(this.ages);
            this.browser.setContent(html);
        }));
        closeButton.setStyle(buttonStyle);
        clearLatestButton.setStyle(buttonStyle);
        final Separator separator = new Separator();
        separator.setMaxWidth(40);
        HBox buttonBox = new HBox();
        buttonBox.getChildren().addAll(clearLatestButton, separator, closeButton);

        VBox root = new VBox();
        root.getChildren().addAll(pickerBox, browser, buttonBox);
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
        return "P" + Y + "Y" + M + "M" + D + "D";
    }



    private final static String setupHtml ="<html><body><h3>Fenomimal Phenopacket generator</h3>" +
            "<p><i>Fenominal</i> allows users to indicate the age of patients by having users indicate the birthdate as" +
            " well as the dates of the medical encounters that are being recorded.<p>" +
            "<p>Fenominal subtracts the birthdate from the encounter dates to get the age of the patient during each encounter." +
            " It does not store or output the birthdate.</p>" +
            "</body></html>";

    public String getHtmlWithAges(List<String> previousAges) {
        StringBuilder builder = new StringBuilder();
        builder.append("<html><body><h3>Fenomimal Phenopacket generator</h3>");
        builder.append("<p>Encounter ages:</p>");
        if (previousAges.isEmpty()) {
            builder.append("<p>You will see encounter ages as the encounters are entered.</p>");
        } else {
            builder.append("<ol>");
            for (String isoAge : previousAges) {
                builder.append("<li>").append(isoAge).append("</li>");
            }
            builder.append("</ol>");
        }
        builder.append("</body></html>");
        return builder.toString();
    }

}
