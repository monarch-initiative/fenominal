package org.monarchinitiative.fenominal.gui.guitools;

import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Callback;
import org.monarchinitiative.fenominal.gui.model.PatientSexAndId;
import org.monarchinitiative.fenominal.gui.model.PatientSexIdAndBirthdate;
import org.monarchinitiative.fenominal.gui.model.Sex;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Optional;

import static java.util.Objects.isNull;

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

    private DatePicker getDPD() {
        final DatePicker datePicker = new DatePicker();
        String pattern = "MM/dd/yyyy";
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

        return datePicker;
    }

    public TextFlow getInitPhenopacketSIB() {
        Text header = new Text("Fenomimal Phenopacket generator\n");

        Text body1 = new Text("""
                Fenominal allows users to indicate the age of patients by having users
                indicate the birthdate as well as the dates of the medical encounters
                that are being recorded.
                 """);
        Text body2 = new Text("""
                Fenominal subtracts the birthdate from the encounter dates to get the
                age of the patient during each encounter.
                """);
        Text body3 = new Text("Fenominal does not store or output the birthdate.");
        header.setFont(Font.font("Verdana", FontWeight.BOLD,18));
        body1.setFont(Font.font("Verdana",FontWeight.NORMAL, 12));
        body2.setFont(Font.font("Verdana",FontWeight.NORMAL, 12));
        body3.setFont(Font.font("Verdana",FontWeight.BOLD, 12));
        TextFlow flow = new TextFlow();
        flow.getChildren().addAll(header, body1, body2, body3);
        return flow;
    }

    public TextFlow getInitPhenopacketSI() {
        Text header = new Text("Fenomimal Phenopacket generator\n");

        Text body1 = new Text("""
                Fenominal allows users to indicate the age of patients by having users
                indicate the birthdate as well as the dates of the medical encounters
                that are being recorded.
                 """);
        Text body2 = new Text("""
                In this mode, users enter the age of the patient at each encounter.
                """);
        header.setFont(Font.font("Verdana", FontWeight.BOLD,18));
        body1.setFont(Font.font("Verdana",FontWeight.NORMAL, 12));
        body2.setFont(Font.font("Verdana",FontWeight.NORMAL, 12));
        TextFlow flow = new TextFlow();
        flow.getChildren().addAll(header, body1, body2);
        return flow;
    }

    /**
     * Gather information about patient sex and id.
     * @return optional object with information about the patient sex and id
     */
    public Optional<PatientSexAndId> showDatePickerDialogSI() {
        Dialog<PatientSexAndId> dialog = new Dialog<>();
        dialog.setTitle("Demographics");
        dialog.setHeaderText("Please enter patient sex and ID");

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setMinWidth(200);
        dialogPane.setMinHeight(300);
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        TextFlow tflow = getInitPhenopacketSI();
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
        VBox vb = new VBox();
        vb.getChildren().addAll(unknownRadio, femaleRadio, maleRadio, otherRadio);
        Separator hseparator = new Separator(Orientation.HORIZONTAL);
        hseparator.setStyle("""
                -fx-padding: 5px;
                    -fx-border-insets: 5px;
                    -fx-background-insets: 5px;""");
        VBox allElementsBox = new VBox();
        allElementsBox.getChildren().addAll(tflow,hseparator, hbox, vb);
        dialogPane.setContent(allElementsBox);
        dialog.setResultConverter((ButtonType button) -> {
            if (button == ButtonType.OK) {
                String patientId = textField.getText().strip();
                Sex sex = Sex.UNKNOWN_SEX;
                if (maleRadio.isSelected()) sex = Sex.MALE;
                else if (femaleRadio.isSelected()) sex = Sex.FEMALE;
                else if (otherRadio.isSelected()) sex = Sex.OTHER_SEX;
                return new PatientSexAndId(sex, patientId);
            }
            return null;
        });

        return dialog.showAndWait();
    }


    /**
     * Gather information about patient sex, id, and birthdate
     * @return
     */
    public Optional<PatientSexIdAndBirthdate> showDatePickerDialogSIB() {
        Dialog<PatientSexIdAndBirthdate> dialog = new Dialog<>();
        dialog.setTitle("Demographics");
        dialog.setHeaderText("Please enter patient sex, ID, and birthdate");

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setMinWidth(300);
        dialogPane.setMinHeight(400);
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        TextFlow tflow = getInitPhenopacketSIB();
        Separator hseparator = new Separator(Orientation.HORIZONTAL);
        hseparator.setStyle("""
                -fx-padding: 5px;
                    -fx-border-insets: 5px;
                    -fx-background-insets: 5px;""");
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

        DatePicker datePicker = getDPD();

        Label selection = new Label("Select birthdate:  ");
        HBox pickerBox = new HBox(selection, datePicker);
        if (isNull(group.getSelectedToggle())) {
            group.selectToggle(unknownRadio);
        }
        VBox vb = new VBox();
        vb.getChildren().addAll(unknownRadio, femaleRadio, maleRadio, otherRadio);
        VBox allElementsBox = new VBox();
        allElementsBox.getChildren().addAll(tflow,hseparator, hbox, vb, pickerBox);
        dialogPane.setContent(allElementsBox);
        dialog.setResultConverter((ButtonType button) -> {
            if (button == ButtonType.OK) {
                String patientId = textField.getText().strip();
                Sex sex = Sex.UNKNOWN_SEX;
                if (maleRadio.isSelected()) sex = Sex.MALE;
                else if (femaleRadio.isSelected()) sex = Sex.FEMALE;
                else if (otherRadio.isSelected()) sex = Sex.OTHER_SEX;
                LocalDate birthdate = datePicker.getValue();
                return new PatientSexIdAndBirthdate(sex, patientId, birthdate);
            }
            return null;
        });
        return dialog.showAndWait();
    }




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
        return new BirthDatePickerDialog("");
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
