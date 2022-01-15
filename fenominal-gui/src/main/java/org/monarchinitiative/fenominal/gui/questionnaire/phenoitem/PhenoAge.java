package org.monarchinitiative.fenominal.gui.questionnaire.phenoitem;

import java.util.Comparator;

public class PhenoAge implements Comparable<PhenoAge> {

    private final int years;
    private final int months;
    private final int days;

    public PhenoAge(int years, int months, int days) {
        this.years = years;
        this.months = months;
        this.days = days;
    }

    public PhenoAge(int years, int months) {
        this(years, months, 0);
    }

    public PhenoAge(int years) {
        this(years, 0, 0);
    }

    public int getYears() {
        return years;
    }

    public int getMonths() {
        return months;
    }

    public int getDays() {
        return days;
    }

    private static final Comparator<PhenoAge> COMPARATOR =
            Comparator.comparingInt(PhenoAge::getYears)
                    .thenComparingInt(PhenoAge::getMonths)
                    .thenComparingInt(PhenoAge::getDays);

    @Override
    public int compareTo(@SuppressWarnings("NullableProblems") PhenoAge other) {
        return COMPARATOR.compare(this, other);
    }

    public boolean initialized() {
        return getYears() != 0 || getMonths() != 0 || getDays() != 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getYears()).append("Y ").append(getMonths()).append("M");
        if (getDays() != 0) {
            sb.append(" ").append(getDays()).append("D");
        }
        return sb.toString();
    }
}
