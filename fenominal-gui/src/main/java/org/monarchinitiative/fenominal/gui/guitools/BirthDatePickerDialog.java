package org.monarchinitiative.fenominal.gui.guitools;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.apache.commons.logging.Log;
import org.monarchinitiative.fenominal.gui.model.PatientSexAndId;
import org.monarchinitiative.fenominal.gui.model.PatientSexIdAndBirthdate;
import org.monarchinitiative.fenominal.gui.model.Sex;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.Objects.isNull;
import static javafx.stage.StageStyle.DECORATED;

public class BirthDatePickerDialog {

    private final Browser browser;

    private LocalDate birthDate =null;

    private String id;

    private Sex sex = Sex.UNKNOWN_SEX;

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



    public Optional<PatientSexIdAndBirthdate> showDatePickerDialog() {
        Dialog<PatientSexIdAndBirthdate> dialog = new Dialog<>();
        dialog.setTitle("Demographics");
        dialog.setHeaderText("Please enter patient sex, ID, and birthdate");
        String pattern = "MM/dd/yyyy";
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        HBox hbox = new HBox();
        Label label = new Label("Patient ID: ");
        TextField textField = new TextField();
        hbox.getChildren().addAll(label, textField);

        ToggleGroup group = new ToggleGroup();
        RadioButton unknownRadio = new RadioButton("Unknown");
        unknownRadio.setToggleGroup(group);
        unknownRadio.setId("unknown");
        RadioButton femaleRadio = new RadioButton("Female");
        femaleRadio.setToggleGroup(group);
        femaleRadio.setId("female");
        RadioButton maleRadio = new RadioButton("Male");
        maleRadio.setToggleGroup(group);
        maleRadio.setId("male");
        RadioButton otherRadio = new RadioButton("Other");
        otherRadio.setToggleGroup(group);
        otherRadio.setId("other");
        final DatePicker datePicker = new DatePicker();
        datePicker.setEditable(false);
        datePicker.setOnAction((actionEvent) -> {
            LocalDate date = datePicker.getValue();
            setBirthDate(date);
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

        if (isNull(group.getSelectedToggle())) {
            group.selectToggle(unknownRadio);
        }
        VBox vb = new VBox();
        vb.getChildren().addAll(unknownRadio, femaleRadio, maleRadio, otherRadio);
        dialogPane.getChildren().addAll(hbox, vb, pickerBox);
        dialog.setResultConverter((ButtonType button) -> {
            if (button == ButtonType.OK) {
                String patientId = textField.getText().strip();
                Sex sex = Sex.fromString(group.getSelectedToggle().toString());
                LocalDate birthdate = datePicker.getValue();
                return new PatientSexIdAndBirthdate(sex, patientId, birthdate);
            }
            return null;
        });

        return dialog.showAndWait();
    }

    public String getId() {
        return id;
    }

    public void setSex(String s) {
        this.sex = Sex.fromString(s);
        System.out.println("SEETING TO " + s);
    }

    public Sex sex() {
        return this.sex;
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
