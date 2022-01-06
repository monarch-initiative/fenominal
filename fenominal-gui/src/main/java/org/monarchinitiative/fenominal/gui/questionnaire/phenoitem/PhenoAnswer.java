package org.monarchinitiative.fenominal.gui.questionnaire.phenoitem;

import org.monarchinitiative.phenol.ontology.data.Term;

import java.util.Optional;

public interface PhenoAnswer {

    Term term();
    Optional<PhenoAge> ageOptional();
    boolean observed();
    boolean excluded();
    boolean unknown();
    String question();

}
