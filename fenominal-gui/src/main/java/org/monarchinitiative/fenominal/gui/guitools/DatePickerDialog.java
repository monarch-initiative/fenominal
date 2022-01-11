package org.monarchinitiative.fenominal.gui.guitools;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static javafx.stage.StageStyle.DECORATED;

public class DatePickerDialog {

    private final String message;

    private final Browser browser;

    private LocalDate birthDate =null;
    private final List<LocalDate> encounterDates;

    private final boolean setBirthDate;

    private final static String buttonStyle =
            " -fx-background-color:" +
            "        linear-gradient(#f2f2f2, #d6d6d6)," +
            "        linear-gradient(#fcfcfc 0%, #d9d9d9 20%, #d6d6d6 100%)," +
            "        linear-gradient(#dddddd 0%, #f6f6f6 50%);" +
            "    -fx-background-radius: 8,7,6;" +
            "    -fx-background-insets: 0,1,2;" +
            "    -fx-text-fill: black;\n" +
            "    -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );";

    public DatePickerDialog(String msg) {
        message = msg;
        browser = new Browser(message);
        setBirthDate = true;
        encounterDates = new ArrayList<>();
    }

    public DatePickerDialog(LocalDate bDate, List<LocalDate> previousEncounters) {
        message = getHtmlWithDates(bDate, previousEncounters);
        browser = new Browser(message);
        setBirthDate = false;
        birthDate = bDate;
        encounterDates = new ArrayList<>(previousEncounters);
    }


    private void setDate(LocalDate d) {
        if (setBirthDate) {
            this.birthDate = d;
        } else {
            encounterDates.add(d);
        }
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
                Period pd = Period.between(birthdate, encounter);
                String age = String.format("%s (Age: %d Y, %d M, %d D)",encounter,  pd.getYears(), pd.getMonths(), pd.getDays());
                builder.append("<li>").append(age).append("</li>");
            }
            builder.append("</ol>");
        }
        builder.append("</body></html>");
        return builder.toString();
    }



    public LocalDate showDatePickerDialog() {
        String pattern = "MM/dd/yyyy";
        final DatePicker datePicker = new DatePicker();
        datePicker.setEditable(false);
        AtomicReference<LocalDate> localDate = new AtomicReference<>();
        datePicker.setOnAction((actionEvent) -> {
            LocalDate date = datePicker.getValue();
            setDate(date);
            localDate.set(date);
            String html = getHtmlWithDates(this.birthDate, this.encounterDates);
            this.browser.setContent(html);
            actionEvent.consume();
        });
        FxDatePickerConverter converter = new FxDatePickerConverter(pattern);
        datePicker.setConverter(converter);
        datePicker.setPromptText(pattern.toLowerCase());

        Callback<DatePicker, DateCell> dayCellFactory = new Callback<>() {
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
        closeButton.setOnAction((actionEvent -> ((Stage)(((Button)actionEvent.getSource()).getScene().getWindow())).close()));
        Button clearLatestButton = new Button("Clear last date");
        clearLatestButton.setOnAction((actionEvent -> {
            int i = this.encounterDates.size() - 1;
            if (i>0) {
                this.encounterDates.remove(i);
            }
            String html = getHtmlWithDates(this.birthDate, this.encounterDates);
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
        return localDate.get();
    }


    static class FxDatePickerConverter extends StringConverter<LocalDate> {
        // Default Date Pattern
        private String pattern = "MM/dd/yyyy";
        private final DateTimeFormatter dtFormatter;

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



    private final static String setupHtml ="<html><body><h3>Fenomimal Phenopacket generator</h3>" +
        "<p><i>Fenominal</i> allows users to indicate the age of patients by having users indicate the birthdate as" +
            " well as the dates of the medical encounters that are being recorded.<p>" +
            "<p>Fenominal subtracts the birthdate from the encounter dates to get the age of the patient during each encounter." +
            " It does not store or output the birthdate.</p>" +
        "</body></html>";

    public static DatePickerDialog getBirthDate() {
        return new DatePickerDialog(setupHtml);
    }


    public static DatePickerDialog getEncounterDate(LocalDate birthdate, List<LocalDate> encounterDates) {
        return new DatePickerDialog(birthdate, encounterDates);
    }

}
