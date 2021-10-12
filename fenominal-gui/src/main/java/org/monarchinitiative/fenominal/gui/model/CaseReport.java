package org.monarchinitiative.fenominal.gui.model;

import java.util.List;


public class CaseReport implements TextMiningResultsModel {

    private List<FenominalTerm> terms;

    public CaseReport() {
    }

    public List<FenominalTerm> getTerms() {
        return terms;
    }

    @Override
    public void addHpoFeatures(List<FenominalTerm> terms) {
        this.terms = List.copyOf(terms);
    }

    @Override
    public int casesMined() {
        return 1;
    }

    @Override
    public int getTermCount() {
        return this.terms.size();
    }
}
