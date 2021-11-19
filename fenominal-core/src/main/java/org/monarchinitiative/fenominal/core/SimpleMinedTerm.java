package org.monarchinitiative.fenominal.core;

import org.monarchinitiative.fenominal.core.corenlp.MappedSentencePart;
import org.monarchinitiative.hpotextmining.core.miners.MinedTerm;
import org.monarchinitiative.phenol.ontology.data.TermId;

/**
 * This represents one Ontology term that has been mined from the current text, indicating the
 * (zero-based) position of the words in the original text that correspond to the Ontology term and
 * whether or not the term was excluded (NOT) in the original text.
 * @author Peter N Robinson
 */
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
