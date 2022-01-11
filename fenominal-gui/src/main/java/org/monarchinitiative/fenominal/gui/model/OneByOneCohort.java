package org.monarchinitiative.fenominal.gui.model;

import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.*;

public class OneByOneCohort implements TextMiningResultsModel {

    private final Map<String, String> data;

    private final List<CaseReport> cases;
    private final Map<TermId, String> labelMap;

    private final String pmid;
    private final String omimId;
    private final String diseasename;


    public  OneByOneCohort(String pmid, String omimId, String diseasename){
        cases = new ArrayList<>();
        labelMap = new HashMap<>();
        this.pmid = pmid;
        this.omimId = omimId;
        this.diseasename = diseasename;
        data = new HashMap<>();
    }

    public void addCase(CaseReport caseReport) {
        cases.add(caseReport);
    }


    public Map<TermId, Integer> getCountsMap() {
        Map<TermId, Integer> countsMap = new HashMap<>();
        for (var report : cases) {
            for (var term : report.getTerms()) {
                TermId hpoId = term.getTerm().getId();
                countsMap.putIfAbsent(hpoId, 0);
                if (term.isObserved()) {
                    countsMap.merge(hpoId, 1, Integer::sum);
                }
                labelMap.putIfAbsent(hpoId, term.getTerm().getName());
            }
        }
        return countsMap;
    }

    public Map<TermId, String> getLabelMap() {
        return labelMap;
    }

    @Override
    public int casesMined() {
        return this.cases.size();
    }

    @Override
    public int getTermCount() {
        Set<TermId> terms = new HashSet<>();
        for (var report : cases) {
            for (var fenominalTerm : report.getTerms()) {
                terms.add(fenominalTerm.getTerm().getId());
            }
        }
        return terms.size();
    }

    @Override
    public Map<String, String> getModelData() {
        return data;
    }

    @Override
    public void setModelDataItem(String k, String v) {
        data.put(k, v);
    }

    @Override
    public void addHpoFeatures(List<FenominalTerm> terms) {
        CaseReport caseReport = new CaseReport();
        caseReport.addHpoFeatures(terms);
        this.addCase(caseReport);
    }

    public String getPmid() {
        return pmid;
    }

    public String getOmimId() {
        return omimId;
    }

    public String getDiseasename() {
        return diseasename;
    }
}
