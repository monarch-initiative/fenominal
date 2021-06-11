package org.monarchinitiative.fenominal.gui;

import org.monarchinitiative.fenominal.core.corenlp.MappedSentencePart;
import org.monarchinitiative.hpotextmining.core.miners.MinedTerm;
import org.monarchinitiative.phenol.ontology.data.TermId;

public class SimpleMinedTerm implements MinedTerm  {

    private final int begin;

    private final int end;

    private final String  termid;

    private final boolean isPresent;



    private SimpleMinedTerm(int begin, int end, TermId tid, boolean isPresent) {
        this.begin = begin;
        this.end = end;
        this.termid = tid.getValue();
        this.isPresent = isPresent;
    }

    public static SimpleMinedTerm fromMappedSentencePart(MappedSentencePart msp) {
        return new SimpleMinedTerm(msp.getStartpos(), msp.getEndpos(), msp.getTid(), true);
    }

    public static SimpleMinedTerm fromExcludedMappedSentencePart(MappedSentencePart msp) {
        return new SimpleMinedTerm(msp.getStartpos(), msp.getEndpos(), msp.getTid(), false);
    }




    @Override
    public int getBegin() {
        return this.begin;
    }

    @Override
    public int getEnd() {
        return this.end;
    }

    @Override
    public String getTermId() {
        return this.termid;
    }

    @Override
    public boolean isPresent() {
        return this.isPresent;
    }
}
