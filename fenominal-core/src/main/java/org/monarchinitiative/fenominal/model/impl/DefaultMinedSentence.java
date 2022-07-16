package org.monarchinitiative.fenominal.model.impl;

import org.monarchinitiative.fenominal.model.MinedSentence;
import org.monarchinitiative.fenominal.model.MinedTermWithMetadata;

import java.util.Collection;

public class DefaultMinedSentence implements MinedSentence {

    private final Collection<MinedTermWithMetadata> minedTerms;
    private final String sentence;

    public DefaultMinedSentence(Collection<MinedTermWithMetadata> terms, String sentence) {
        this.minedTerms = terms;
        this.sentence = sentence;
    }



    @Override
    public Collection<MinedTermWithMetadata> getMinedTerms() {
        return  minedTerms;
    }

    @Override
    public String getText() {
        return sentence;
    }
}
