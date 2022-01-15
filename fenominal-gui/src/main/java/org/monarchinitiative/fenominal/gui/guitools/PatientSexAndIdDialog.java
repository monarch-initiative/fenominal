package org.monarchinitiative.fenominal.gui.guitools;

import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.monarchinitiative.fenominal.gui.model.PatientSexAndId;
import org.monarchinitiative.fenominal.gui.model.Sex;

import java.util.Optional;

import static java.util.Objects.isNull;

public class PatientSexAndIdDialog {

    public static Optional<PatientSexAndId> show() {
        Dialog<PatientSexAndId> dialog = new Dialog<>();
        dialog.setTitle("Demographics");
        dialog.setHeaderText("Please enter patient sex and ID");
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

        if (isNull(group.getSelectedToggle())) {
            group.selectToggle(unknownRadio);
        }
        VBox vb = new VBox();
        vb.getChildren().addAll(unknownRadio, femaleRadio, maleRadio, otherRadio);
        dialogPane.getChildren().addAll(hbox, vb);
        dialog.setResultConverter((ButtonType button) -> {
            if (button == ButtonType.OK) {
                String patientId = textField.getText().strip();
                Sex sex = Sex.fromString(group.getSelectedToggle().toString());
                return new PatientSexAndId(sex, patientId);
            }
            return null;
        });

        Optional<PatientSexAndId> result = dialog.showAndWait();
        return result;
    }




    /*



        DatePicker datePicker = new DatePicker(LocalDate.now());
        ObservableList<Venue> options =
            FXCollections.observableArrayList(Venue.values());
        ComboBox<Venue> comboBox = new ComboBox<>(options);
        comboBox.getSelectionModel().selectFirst();
        dialogPane.setContent(new VBox(8, textField, datePicker, comboBox));
        Platform.runLater(textField::requestFocus);
        dialog.setResultConverter((ButtonType button) -> {
            if (button == ButtonType.OK) {
                return new Results(textField.getText(),
                    datePicker.getValue(), comboBox.getValue());
            }
            return null;
        });
        Optional<Results> optionalResult = dialog.showAndWait();
        optionalResult.ifPresent((Results results) -> {
            System.out.println(
                results.text + " " + results.date + " " + results.venue);
        });
     */

}
