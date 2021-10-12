package org.monarchinitiative.fenominal.gui.guitools;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static javafx.stage.StageStyle.DECORATED;

public class DatePickerDialog {

    private final TextArea textArea = new TextArea();

    private LocalDate date;

    private final String message;

    private final Browser browser;

    private LocalDate birthdate =null;
    private List<LocalDate> encounterDates;

    private final boolean setBirthDate;

    public DatePickerDialog(String msg) {
        message = msg;
        browser = new Browser(message);
        setBirthDate = true;
        encounterDates = new ArrayList<>();
    }

    public DatePickerDialog(LocalDate birthDate, List<LocalDate> previousEncounters) {
        message = getHtmlWithDates(birthDate, previousEncounters);
        browser = new Browser(message);
        setBirthDate = false;
        birthDate = birthdate;
        encounterDates = List.copyOf(previousEncounters);
        encounterDates = new ArrayList<>();
    }

    private void setDate(LocalDate d) {
        if (setBirthDate) {
            this.birthdate = d;
        } else {
            encounterDates.add(d);
        }
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public List<LocalDate> getEncounterDates() {
        return encounterDates;
    }


    public String getHtmlWithDates(LocalDate birthdate, List<LocalDate> encounterDates) {
        StringBuilder builder = new StringBuilder();
        builder.append("<html><body><h3>Fenomimal Phenopacket generator</h3>");
        builder.append("<p>Fenominal subtracts the birthdate from the encounter dates to get the age of the patient during each encounter." +
                " It does not store or output the birthdate.</p>");
        builder.append("<p>Birthdate: ").append(birthdate.toString()).append("</p>");
        if (encounterDates.isEmpty()) {
            builder.append("<p>You will see encounter dates and ages as the encounters are entered.</p>");
        } else {
            builder.append("<ol>");
            for (LocalDate encounter: encounterDates) {
                builder.append("<li>").append(encounter.toString()).append("</li>");
            }
            builder.append("</ol>");
        }
        builder.append("</body></html>");
        return builder.toString();
    }



    public LocalDate getLocalDate() {
        String pattern = "MM/dd/yyyy";
        final DatePicker datePicker = new DatePicker();
        datePicker.setEditable(false);
        datePicker.setOnAction((actionEvent) -> {
            LocalDate date = datePicker.getValue();
            setDate(date);
            String html = getHtmlWithDates(this.birthdate, this.encounterDates);
            this.browser.setContent(html);
            actionEvent.consume();
        });
        FxDatePickerConverter converter = new FxDatePickerConverter(pattern);
        datePicker.setConverter(converter);
        datePicker.setPromptText(pattern.toLowerCase());

        // Create a day cell factory
        Callback<DatePicker, DateCell> dayCellFactory = new Callback<DatePicker, DateCell>() {
            public DateCell call(final DatePicker datePicker) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);
                        DayOfWeek day = DayOfWeek.from(item);
                        if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
                            this.setTextFill(Color.BLUE);
                        }
                    }
                };
            }
        };
        datePicker.setDayCellFactory(dayCellFactory);
        Label selection = new Label("Select date:");
        HBox pickerBox = new HBox(selection, datePicker);

        Button closeButton = new Button("Done");
        closeButton.setOnAction((actionEvent -> {
            ((Stage)(((Button)actionEvent.getSource()).getScene().getWindow())).close();
        }));
        Button clearLatestButton = new Button("Clear last date");
        clearLatestButton.setOnAction((actionEvent -> {
            int i = this.encounterDates.size() - 1;
            if (i>0) {
                this.encounterDates.remove(i);
            }
            String html = getHtmlWithDates(this.birthdate, this.encounterDates);
            this.browser.setContent(html);
        }));
        closeButton.setStyle("-fx-padding: 20 20 20 20;");
        clearLatestButton.setStyle("-fx-padding: 20 20 20 20;");
        HBox buttonBox = new HBox();
        buttonBox.getChildren().addAll(clearLatestButton, closeButton);

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
        return date;
    }


    static class FxDatePickerConverter extends StringConverter<LocalDate> {
        // Default Date Pattern
        private String pattern = "MM/dd/yyyy";
        private DateTimeFormatter dtFormatter;

        public FxDatePickerConverter() {
            dtFormatter = DateTimeFormatter.ofPattern(pattern);
        }

        public FxDatePickerConverter(String pattern) {
            this.pattern = pattern;
            dtFormatter = DateTimeFormatter.ofPattern(pattern);
        }

        // Change String to LocalDate
        public LocalDate fromString(String text) {
            LocalDate date = null;
            if (text != null && !text.trim().isEmpty()) {
                date = LocalDate.parse(text, dtFormatter);
            }
            return date;
        }

        // Change LocalDate to String
        @Override
        public String toString(LocalDate date) {
            String text = "n/a";
            if (date != null) {
                text = dtFormatter.format(date);
            }
            return text;
        }
    }


    class Browser extends Region {

        final WebView browser = new WebView();
        final WebEngine webEngine = browser.getEngine();

        public Browser(String html) {
            getStyleClass().add("browser");
            webEngine.loadContent(html);
            getChildren().add(browser);
        }
        private Node createSpacer() {
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            return spacer;
        }

        @Override protected void layoutChildren() {
            double w = getWidth();
            double h = getHeight();
            layoutInArea(browser,0,0,w,h,0, HPos.CENTER, VPos.CENTER);
        }

        @Override protected double computePrefWidth(double height) {
            return 750;
        }

        @Override protected double computePrefHeight(double width) {
            return 500;
        }

        public void setContent(String html) {
            webEngine.loadContent(html);
        }
    }


    private final static String setupHtml ="<html><body><h3>Fenomimal Phenopacket generator</h3>" +
        "<p><i>Fenominal</i> allows users to indicate the age of patients by having users indicate the birthdate as" +
            " well as the dates of the medical encounters that are being recorded.<p>" +
            "<p>Fenominal subtracts the birthdate from the encounter dates to get the age of the patient during each encounter." +
            " It does not store or output the birthdate.</p>" +
        "</body></html>";

    public static DatePickerDialog getBirthDate() {
        return new DatePickerDialog(setupHtml);
    }

}
