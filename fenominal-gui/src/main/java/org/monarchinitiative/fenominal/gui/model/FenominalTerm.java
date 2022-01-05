package org.monarchinitiative.fenominal.gui.model;

import org.monarchinitiative.fenominal.gui.hpotextminingwidget.PhenotypeTerm;
import org.monarchinitiative.hpotextmining.gui.controller.Main;
import org.monarchinitiative.phenol.ontology.data.Term;

import java.util.Objects;

/**
 * Encapsulates one (text-mined) HPO term and a boolean to indicate
 * whether the term was observed or excluded.
 * @author  Peter N Robinson
 */
public record FenominalTerm(Term term, boolean observed)
        implements Comparable<FenominalTerm> {

    public Term getTerm() {
        return term;
    }

    public boolean isObserved() {
        return observed;
    }

    public static FenominalTerm fromMainPhenotypeTerm(PhenotypeTerm mpt) {
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
