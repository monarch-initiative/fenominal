package org.monarchinitiative.fenominal.gui.guitools;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicReference;

import static javafx.stage.StageStyle.DECORATED;

public class BirthDatePickerDialog {

    private final Browser browser;

    private LocalDate birthDate =null;

    private String id;

    private final boolean isUpdate ;

    private final static String buttonStyle =
            " -fx-background-color:" +
                    "        linear-gradient(#f2f2f2, #d6d6d6)," +
                    "        linear-gradient(#fcfcfc 0%, #d9d9d9 20%, #d6d6d6 100%)," +
                    "        linear-gradient(#dddddd 0%, #f6f6f6 50%);" +
                    "    -fx-background-radius: 8,7,6;" +
                    "    -fx-background-insets: 0,1,2;" +
                    "    -fx-text-fill: black;\n" +
                    "    -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );";

    /**
     * This is called for initializing a case
     */
    public BirthDatePickerDialog(String msg) {
        this(msg, false, null);
    }

    /**
     * This is called for updating an existing phenopacket.
     */
    public BirthDatePickerDialog(String msg, boolean isUpdate, String id) {
        this.isUpdate = isUpdate;
        browser = new Browser(msg);
        this.id = id;
    }



    private void setBirthDate(LocalDate d) {
       this.birthDate = d;
    }


    public String getHtmlWithDates(LocalDate birthdate) {
        return "<html><body><h3>Fenomimal Phenopacket generator</h3>" +
                "<p>Fenominal subtracts the birthdate from the encounter dates to get the age of the patient during each encounter." +
                " It does not store or output the birthdate.</p>" +
                "<p>Birthdate: " + birthdate.toString() + "</p>" +
                "</body></html>";
    }



    public LocalDate showDatePickerDialog() {
        String pattern = "MM/dd/yyyy";
        final DatePicker datePicker = new DatePicker();
        datePicker.setEditable(false);
        AtomicReference<LocalDate> localDate = new AtomicReference<>();
        datePicker.setOnAction((actionEvent) -> {
            LocalDate date = datePicker.getValue();
            setBirthDate(date);
            localDate.set(date);
            String html = getHtmlWithDates(this.birthDate);
            this.browser.setContent(html);
            actionEvent.consume();
        });
        DatePickerDialog.FxDatePickerConverter converter = new DatePickerDialog.FxDatePickerConverter(pattern);
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
        Label selection = new Label("Select birthdate:  ");
        HBox pickerBox = new HBox(selection, datePicker);

        Button closeButton = new Button("Done");
        closeButton.setOnAction((actionEvent -> ((Stage)(((Button)actionEvent.getSource()).getScene().getWindow())).close()));
        Button clearLatestButton = new Button("Clear last date");
        clearLatestButton.setOnAction((actionEvent -> {
            String html = getHtmlWithDates(this.birthDate);
            this.browser.setContent(html);
        }));
        closeButton.setStyle(buttonStyle);
        clearLatestButton.setStyle(buttonStyle);
        final Separator separator = new Separator();
        separator.setMaxWidth(40);
        HBox idBox = new HBox();
        TextField idField = null;
        if (isUpdate) {
            Text t = new Text(String.format("Using existing phenopacket id %s", this.id));
            idBox.getChildren().add(t);
        } else {
            Label idLabel = new Label("Enter patient id:  ");
            idField = new TextField();
            idBox.getChildren().addAll(idLabel, separator, idField);
        }

        HBox buttonBox = new HBox();
        buttonBox.getChildren().addAll(clearLatestButton, separator, closeButton);



        VBox root = new VBox();
        root.getChildren().addAll(pickerBox, browser,idBox,  buttonBox);
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
        if (! isUpdate) {
            this.id = idField.getText();
        }
        return localDate.get();
    }

    public String getId() {
        return id;
    }



    private final static String setupHtml ="<html><body><h3>Fenomimal Phenopacket generator</h3>" +
            "<p><i>Fenominal</i> allows users to indicate the age of patients by having users indicate the birthdate as" +
            " well as the dates of the medical encounters that are being recorded.<p>" +
            "<p>Fenominal subtracts the birthdate from the encounter dates to get the age of the patient during each encounter." +
            " It does not store or output the birthdate.</p>" +
            "</body></html>";

    private final static String updateHtml = """
            <html><body><h3>Fenomimal Phenopacket generator</h3>
            <p><i>Fenominal</i> allows users to indicate the age of patients by having users indicate the birthdate as
             well as the dates of the medical encounters that are being recorded.<p>
            <p>Fenominal subtracts the birthdate from the encounter dates to get the age of the patient during each encounter.
            It does not store or output the birthdate.</p>
            <p><b>You are updating an existing Phenopacket. It is essential that you use the same birthdate.</b></p>
            <p>Fenominal cannot check this because it does not store the birthdate for privacy reasons!</p>
             </body></html>
            """;

    public static BirthDatePickerDialog getBirthDate() {
        return new BirthDatePickerDialog(setupHtml);
    }

    /**
     * This method is called when we import an existing Phenopacket
     * @param id Identifier of the pre-existing phenopacket
     * @return birthdate picker, which qllows clients to retrieve birthdate and id
     */
    public static BirthDatePickerDialog getBirthDate(String id) {
        return new BirthDatePickerDialog(updateHtml, true, id);
    }



}
