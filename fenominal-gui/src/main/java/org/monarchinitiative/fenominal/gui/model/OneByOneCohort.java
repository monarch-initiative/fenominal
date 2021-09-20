package org.monarchinitiative.fenominal.gui.model;

import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.*;

public class OneByOneCohort implements TextMiningResultsModel {

    private final List<CaseReport> cases;
    private final Map<TermId, String> labelMap;

    public  OneByOneCohort(){
        cases = new ArrayList<>();
        labelMap = new HashMap<>();
    }

    public void addCase(CaseReport caseReport) {
        cases.add(caseReport);
    }


    private Map<TermId, Integer> getCountsMap() {
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

    @Override
    public int minedSoFar() {
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
    public void output() {
        System.out.println(getTsv());
    }

    @Override
    public List<String> getTsv() {
        Map<TermId, Integer> countsMap = getCountsMap();
        int N = cases.size();
        List<String> rows = new ArrayList<>();
        rows.add(String.format("%s\t%s\t%s\t", "Id", "Term", "Counts"));
        for (var e : countsMap.entrySet()) {
            TermId tid = e.getKey();
            String label = labelMap.get(tid);
            String counts = String.format("%d/%d", e.getValue(), N);
            rows.add(String.format("%s\t%s\t%s\t", tid.getValue(), label, counts));
        }
        return rows;
    }

    @Override
    public void addHpoFeatures(List<FenominalTerm> terms) {
        CaseReport caseReport = new CaseReport();
        caseReport.addHpoFeatures(terms);
        this.addCase(caseReport);
    }
}
