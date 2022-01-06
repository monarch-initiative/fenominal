package org.monarchinitiative.fenominal.gui.questionnaire.qtable;

import javafx.beans.property.BooleanProperty;
import javafx.scene.control.ToggleButton;
import org.controlsfx.control.SegmentedButton;
import org.monarchinitiative.fenominal.gui.questionnaire.phenoitem.AnswerType;


public class TermSelectionButton extends SegmentedButton {

    private final ToggleButton observedButton = new ToggleButton("Y");
    private final ToggleButton excludedButton = new ToggleButton("N");
    private final ToggleButton unobservedButton = new ToggleButton("U");

    public TermSelectionButton() {
        initGraphics();
    }

    private void initGraphics() {
        observedButton.setFocusTraversable(false);
        observedButton.setId("observed");
        excludedButton.setFocusTraversable(false);
        excludedButton.setId("excluded");
        unobservedButton.setFocusTraversable(false);
        unobservedButton.setId("unobserved");
        getButtons().addAll(observedButton, excludedButton, unobservedButton);
        setFocusTraversable(false);
        getStyleClass().add(SegmentedButton.STYLE_CLASS_DARK);
    }

    public BooleanProperty includedProperty() {
        return observedButton.selectedProperty();
    }

    public BooleanProperty excludedProperty() {
        return excludedButton.selectedProperty();
    }

    public BooleanProperty unobservedProperty() {
        return unobservedButton.selectedProperty();
    }

    public AnswerType getAnswer() {
        if (includedProperty().get()) {
            return AnswerType.OBSERVED;
        } else if (excludedProperty().get()) {
            return AnswerType.EXCLUDED;
        } else {
            return AnswerType.UNKNOWN;
        }
    }

}
