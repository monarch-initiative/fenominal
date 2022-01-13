package org.monarchinitiative.fenominal.gui.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.monarchinitiative.fenominal.gui.config.FenominalConfig.PATIENT_ID_KEY;

public interface TextMiningResultsModel {

    void addHpoFeatures(List<FenominalTerm> terms);

    /** currently only used by Phenopacket, but shold be expanded. */
    default void addHpoFeatures(List<FenominalTerm> terms, LocalDate date) {}

    /** currently only used by Phenopacket, but shold be expanded. */
    default void addHpoFeatures(List<FenominalTerm> terms, String isoAge) {}

    int casesMined();

    int getTermCount();

    Map<String,String> getModelData();

    void setModelDataItem(String k, String v);

    default String getInitialFileName() {
        Map<String, String> data = getModelData();
        if (data.containsKey(PATIENT_ID_KEY)) {
            return data.get(PATIENT_ID_KEY) + "-" + "fenominal.json";
        } else {
            return "fenominal.json";
        }
    }

}
