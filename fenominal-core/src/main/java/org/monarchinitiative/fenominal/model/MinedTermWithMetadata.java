package org.monarchinitiative.fenominal.model;

import org.monarchinitiative.phenol.ontology.data.TermId;

public interface MinedTermWithMetadata extends MinedTerm {

    String getMatchingString();
    double getSimilarity();

    TermId getTermId();

    int getTokenCount();
}
