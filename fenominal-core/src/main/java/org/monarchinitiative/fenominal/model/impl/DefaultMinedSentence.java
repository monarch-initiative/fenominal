package org.monarchinitiative.fenominal.model.impl;

import org.monarchinitiative.fenominal.model.MinedSentence;
import org.monarchinitiative.fenominal.model.MinedTermWithMetadata;

import java.util.Collection;

public class DefaultMinedSentence implements MinedSentence {

    private final Collection<? extends MinedTermWithMetadata> minedTerms;
    private final String sentence;

    public DefaultMinedSentence(Collection<? extends MinedTermWithMetadata> terms, String sentence) {
        this.minedTerms = terms;
        this.sentence = sentence;
    }



    @Override
    public Collection<? extends MinedTermWithMetadata> getMinedTerms() {
        return  minedTerms;
    }

    @Override
    public String getText() {
        return sentence;
    }
}
