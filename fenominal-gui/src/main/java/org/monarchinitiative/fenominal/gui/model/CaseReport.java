package org.monarchinitiative.fenominal.gui.model;

import java.util.ArrayList;
import java.util.List;


public class CaseReport implements TextMiningResultsModel {

    private List<FenominalTerm> terms;

    public CaseReport() {
    }

    public List<FenominalTerm> getTerms() {
        return terms;
    }

    @Override
    public void output() {
        System.out.println(getTsv());
    }

    @Override
    public List<String> getTsv() {
        List<String> rows = new ArrayList<>();
        for (var mt : terms) {
            rows.add(mt.toString());
        }
        return rows;
    }

    @Override
    public void addHpoFeatures(List<FenominalTerm> terms) {
        this.terms = List.copyOf(terms);
    }

    @Override
    public int minedSoFar() {
        return 1;
    }

    @Override
    public int getTermCount() {
        return this.terms.size();
    }
}
