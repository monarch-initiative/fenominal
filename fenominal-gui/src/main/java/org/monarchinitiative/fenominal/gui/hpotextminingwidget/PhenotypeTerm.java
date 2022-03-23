package org.monarchinitiative.fenominal.gui.hpotextminingwidget;

import org.monarchinitiative.fenominal.core.MinedTerm;
import org.monarchinitiative.phenol.ontology.data.Term;

import java.util.Objects;

public class PhenotypeTerm {

    private final Term term;

    private final int begin, end;

    private final boolean present;

    public PhenotypeTerm(Term term, MinedTerm minedTerm) {
        this.term = term;
        this.begin = minedTerm.getBegin();
        this.end = minedTerm.getEnd();
        this.present = minedTerm.isPresent();
    }

    public PhenotypeTerm(Term term, boolean present) {
        this(term, -1, -1, present);
    }

    public PhenotypeTerm(Term term, int begin, int end, boolean present) {
        this.term = term;
        this.begin = begin;
        this.end = end;
        this.present = present;
    }

    public PhenotypeTerm(PhenotypeTerm other, boolean present) {
        this.term = other.term;
        this.begin = other.begin;
        this.end = other.end;
        this.present = present;
    }

    public int getBegin() {
        return begin;
    }

    public int getEnd() {
        return end;
    }

    public boolean isPresent() {
        return present;
    }

    public Term getTerm() {
        return term;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PhenotypeTerm that = (PhenotypeTerm) o;
        return term.id().getValue().equals(that.term.id().getValue()) &&
                begin == that.begin &&
                end == that.end &&
                present == that.present;
    }

    @Override
    public int hashCode() {
        return Objects.hash(term.id().getValue(), begin, end, present);
    }

    @Override
    public String toString() {
        return "PhenotypeTerm{" +
                "term=" + term +
                ", begin=" + begin +
                ", end=" + end +
                ", present=" + present +
                '}';
    }
}
