package org.monarchinitiative.fenominal.gui.model;

import java.util.List;


public class CaseReport implements TextMiningResultsModel {

    private List<FenominalTerm> terms;

    private final String caseId;
    private final String isoAge;

    public CaseReport(String id, String age) {
        this.caseId = id;
        this.isoAge = age;
    }
    public CaseReport() {
        // used by the OneByOneCohort, this is ugly, refactor
        this("n/a", "n/a");
    }

    public List<FenominalTerm> getTerms() {
        return terms == null ? List.of() : terms;
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
        return terms == null ? 0 : this.terms.size();
    }

    public String getCaseId() {
        return caseId;
    }

    public String getIsoAge() {
        return isoAge;
    }
}
