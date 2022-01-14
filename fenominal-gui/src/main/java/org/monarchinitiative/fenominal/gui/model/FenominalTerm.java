package org.monarchinitiative.fenominal.gui.model;

import org.monarchinitiative.fenominal.core.FenominalRunTimeException;
import org.monarchinitiative.fenominal.gui.hpotextminingwidget.PhenotypeTerm;
import org.monarchinitiative.phenol.ontology.data.Term;

import java.time.Period;

/**
 * Encapsulates one (text-mined) HPO term and a boolean to indicate
 * whether the term was observed or excluded.
 * @author  Peter N Robinson
 */
public class FenominalTerm implements Comparable<FenominalTerm> {

    private final Term term;
    private final Period age;

    private final boolean observed;

    public FenominalTerm(Term term, Period age, boolean observed) {
        this.term = term;
        this.age = age;
        this.observed = observed;
    }

    public FenominalTerm(Term term, boolean observed) {
        this.term = term;
        this.age = null;
        this.observed = observed;
    }

    public Term getTerm() {
        return term;
    }

    public boolean isObserved() {
        return observed;
    }

    public static FenominalTerm fromMainPhenotypeTerm(PhenotypeTerm mpt) {
        return new FenominalTerm(mpt.getTerm(), null, mpt.isPresent());
    }

    public static FenominalTerm fromMainPhenotypeTermWithAge(PhenotypeTerm mpt, Period age) {
        return new FenominalTerm(mpt.getTerm(), age, mpt.isPresent());
    }

    public static FenominalTerm fromMainPhenotypeTermWithIsoAge(PhenotypeTerm mpt, Period iso8601period) {
        return new FenominalTerm(mpt.getTerm(), iso8601period, mpt.isPresent());
    }

    @Override
    public int compareTo(FenominalTerm that) {
        return this.term.getName().compareTo(that.term.getName());
    }

    @Override
    public String toString() {
        return String.format("%s (%s): %s", term.getName(), term.getId().getValue(), (this.observed ? "observed" : "excluded"));
    }

    public boolean hasAge() {
        return age != null;
    }

    public Period getAge() {
        return age;
    }

    public String getIso8601Age() {
        if (age == null) {
            throw new FenominalRunTimeException("Attempt to get isoAge string although age is null");
        }
        int y = age.getYears();
        int m = age.getMonths();
        int d = age.getDays();
        if (d==0) {
            if (m==0) {
                return String.format("P%dY", y);
            } else {
                return String.format("P%dY%dM", y, m);
            }
        } else {
            return String.format("P%dY%dM%dD", y, m, d);
        }
    }
}
