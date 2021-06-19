package org.monarchinitiative.fenominal.gui.model;

import org.monarchinitiative.hpotextmining.gui.controller.Main;
import org.monarchinitiative.phenol.ontology.data.Term;

import java.util.Objects;

public class FenominalTerm implements Comparable<FenominalTerm> {

    private final Term term;
    private final boolean observed;

    private FenominalTerm(Term term, boolean observed){
        this.term = term;
        this.observed = observed;
    }

    public Term getTerm() {
        return term;
    }

    public boolean isObserved() {
        return observed;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.term, this.observed);
    }

    @Override
    public boolean equals(Object obj) {
        if (! (obj instanceof FenominalTerm that)) return false;
        return this.term.equals(that.term) && this.observed == that.observed;
    }

    public static FenominalTerm fromMainPhenotypeTerm(Main.PhenotypeTerm mpt) {
        return new FenominalTerm(mpt.getTerm(), mpt.isPresent());
    }

    @Override
    public int compareTo(FenominalTerm that) {
        return this.term.getName().compareTo(that.term.getName());
    }

    @Override
    public String toString() {
        return String.format("%s (%s): %s", term.getName(), term.getId().getValue(), (this.observed ? "observed" : "excluded"));
    }
}
