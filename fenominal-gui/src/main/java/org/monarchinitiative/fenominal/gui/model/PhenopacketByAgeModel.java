package org.monarchinitiative.fenominal.gui.model;

import java.util.*;

import static org.monarchinitiative.fenominal.gui.config.FenominalConfig.N_CURATED_KEY;
import static org.monarchinitiative.fenominal.gui.config.FenominalConfig.PATIENT_ID_KEY;

public class PhenopacketByAgeModel  implements TextMiningResultsModel {

    private final List<FenominalTerm> terms;


    private int casesMined = 0;

    private final Map<String, String> data;

    public PhenopacketByAgeModel(String id) {
        terms = new ArrayList<>();
        data = new HashMap<>();
        data.put(PATIENT_ID_KEY, id);
    }

    @Override
    public void addHpoFeatures(List<FenominalTerm> fterms) {
        this.terms.addAll(fterms);
        casesMined++;
        setModelDataItem(N_CURATED_KEY, String.valueOf(getTermCount()));
    }


    @Override
    public int casesMined() {
        return casesMined;
    }

    @Override
    public int getTermCount() {
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

    public List<FenominalTerm> getTerms() {
        return terms;
    }


    public List<String> getEncounterAges() {
        Set<String> ageSet = new HashSet<>();
        for (FenominalTerm fenominalTerm : terms) {
            if (fenominalTerm.hasAge()) {
                ageSet.add(fenominalTerm.getIso8601Age());
            }
        }
        List<String> ageList = new ArrayList<>(ageSet);
        Collections.sort(ageList);
        return ageList;
    }
}
