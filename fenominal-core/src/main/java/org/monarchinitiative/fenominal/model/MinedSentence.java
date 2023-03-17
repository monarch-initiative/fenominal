package org.monarchinitiative.fenominal.model;

import java.util.Collection;

public interface MinedSentence {

    Collection<? extends MinedTermWithMetadata> getMinedTerms();
    String getText();
}
