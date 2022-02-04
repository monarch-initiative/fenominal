package org.monarchinitiative.fenominal.gui.questionnaire.phenoitem;

import org.monarchinitiative.phenol.ontology.data.Term;

import java.util.Optional;

/**
 * This represents a question about a Phenotypic Abnormality that will be
 * transformed into a single table row and presented to the User.
 */
public interface PhenoItem {
    String termLabel();
    // Additional question, intended for Age-Rule phenoitems
     String question();
     String explanation();

    Term term();
    AnswerType answer();
    void updateAnswer(AnswerType answer);
    default void updateAge(PhenoAge age) {}
    default boolean isUnknown() { return answer().equals(AnswerType.UNKNOWN); }
    default boolean isObserved() { return answer().equals(AnswerType.OBSERVED); }
    default boolean isExcluded() { return answer().equals(AnswerType.EXCLUDED); }
}
