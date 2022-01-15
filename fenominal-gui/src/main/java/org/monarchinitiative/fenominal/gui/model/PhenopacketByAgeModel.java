package org.monarchinitiative.fenominal.gui.model;

import org.monarchinitiative.fenominal.gui.io.PhenopacketImporter;

import java.util.*;

import static org.monarchinitiative.fenominal.gui.config.FenominalConfig.N_CURATED_KEY;
import static org.monarchinitiative.fenominal.gui.config.FenominalConfig.PATIENT_ID_KEY;

public class PhenopacketByAgeModel extends AbstractPhenopacketModel implements TextMiningResultsModel {

    private final List<FenominalTerm> terms;

    private int casesMined = 0;

    public PhenopacketByAgeModel(String id) {
        super(id);
        terms = new ArrayList<>();
        data.put(PATIENT_ID_KEY, id);
    }

    public PhenopacketByAgeModel(PhenopacketImporter importer) {
        super(importer);
        terms = new ArrayList<>();
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
