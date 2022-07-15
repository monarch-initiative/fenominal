package org.monarchinitiative.fenominal.model.impl;

import org.monarchinitiative.fenominal.core.impl.decorators.Decorations;
import org.monarchinitiative.fenominal.model.MinedTerm;
import org.monarchinitiative.phenol.ontology.data.TermId;

/**
 * This represents one Ontology term that has been mined from the current text, indicating the
 * (zero-based) position of the words in the original text that correspond to the Ontology term and
 * whether or not the term was excluded (NOT) in the original text.
 * @author Peter N Robinson
 */
public class DefaultMinedTerm implements MinedTerm {

    private final int begin;

    private final int end;

    private final TermId termid;

    private final boolean isPresent;


    public DefaultMinedTerm(int begin, int end, String tid, boolean isPresent) {
        this.begin = begin;
        this.end = end;
        this.termid = TermId.of(tid);
        this.isPresent = isPresent;
    }
    DefaultMinedTerm(int begin, int end, TermId tid, boolean isPresent) {
        this.begin = begin;
        this.end = end;
        this.termid = tid;
        this.isPresent = isPresent;
    }

    public static DefaultMinedTerm fromMappedSentencePart(DetailedMinedTerm msp) {
        boolean observed = !msp.getDecorations().containsKey(Decorations.NEGATION.name()) ||
                !msp.getDecorations().get(Decorations.NEGATION.name()).equals("true");
        return new DefaultMinedTerm(msp.getBegin(), msp.getEnd(), msp.getTermIdAsString(), observed);
    }

    public static DefaultMinedTerm fromExcludedMappedSentencePart(DetailedMinedTerm msp) {
        return new DefaultMinedTerm(msp.getBegin(), msp.getEnd(), msp.getTermIdAsString(), false);
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
    public String getTermIdAsString() {
        return this.termid.getValue();
    }

    @Override
    public boolean isPresent() {
        return this.isPresent;
    }

    @Override
    public String toString() {
        return String.format("%s [%d-%d]%s", termid, begin, end, (isPresent?"":"excluded"));
    }
}
