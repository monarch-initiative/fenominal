package org.monarchinitiative.fenominal.gui.model;

import org.monarchinitiative.phenol.ontology.data.TermId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class OneByOneCohort implements TextMiningResultsModel {
    private final static Logger LOG = LoggerFactory.getLogger(OneByOneCohort.class);
    private final Map<String, String> data;

    private final List<CaseReport> cases;
    private final Map<TermId, String> labelMap;

    private final String pmid;
    private final String omimId;
    private final String diseasename;

    private boolean changed;


    public  OneByOneCohort(String pmid, String omimId, String diseasename){
        cases = new ArrayList<>();
        labelMap = new HashMap<>();
        this.pmid = pmid;
        this.omimId = omimId;
        this.diseasename = diseasename;
        data = new HashMap<>();
        changed = false;
    }

    public void addCase(CaseReport caseReport) {
        changed = true;
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
        changed = true;
        data.put(k, v);
    }

    @Override
    public void addHpoFeatures(List<FenominalTerm> terms) {
        CaseReport caseReport = new CaseReport();
        caseReport.addHpoFeatures(terms);
        this.addCase(caseReport);
        changed = true;
    }

    @Override
    public String getInitialFileName() {
        String fname =  pmid + "-" + diseasename.replaceAll(" ", "_") + "-fenominal.json";
        LOG.info("Cohort, initial file name: {}", fname);
        return fname;
    }

    @Override
    public boolean isChanged() {
        return changed;
    }

    @Override
    public void resetChanged() {
        changed = false;
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
