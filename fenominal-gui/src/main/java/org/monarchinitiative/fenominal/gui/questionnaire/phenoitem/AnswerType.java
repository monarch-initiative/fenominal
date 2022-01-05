package org.monarchinitiative.fenominal.gui.questionnaire.phenoitem;

/**
 * The answer that the user supplies (or that we infer based on the age entered by the user)
 * @author Peter N Robinson
 */
public enum AnswerType {

    OBSERVED, EXCLUDED, UNKNOWN;

    @Override
    public String toString() {
        return switch (this) {
            case UNKNOWN -> "U";
            case OBSERVED -> "O";
            case EXCLUDED -> "E";
        };
    }
}
