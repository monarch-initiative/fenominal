package org.monarchinitiative.fenominal.model;

import java.util.Collection;

public interface MinedSentence {

    Collection<MinedTermWithMetadata> getMinedTerms();
    String getText();
}
