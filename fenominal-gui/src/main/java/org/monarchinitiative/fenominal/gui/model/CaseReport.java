package org.monarchinitiative.fenominal.gui.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CaseReport implements TextMiningResultsModel {

    private List<FenominalTerm> terms;

    private final String caseId;
    private final String isoAge;

    private final Map<String, String> data;

    private boolean changed;

    public CaseReport(String id, String age) {
        this.caseId = id;
        this.isoAge = age;
        data = new HashMap<>();
        changed = false;
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
        changed = true;
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

    @Override
    public Map<String, String> getModelData() {
        return data;
    }

    @Override
    public void setModelDataItem(String k, String v) {
        changed = true;
        data.put(k, v);
    }

    @Override
    public String getInitialFileName() {
        return caseId + "-fenominal.json";
    }

    @Override
    public boolean isChanged() {
        return changed;
    }

    @Override
    public void resetChanged() {
        changed = true;
    }

    public String getCaseId() {
        return caseId;
    }

    public String getIsoAge() {
        return isoAge;
    }
}
