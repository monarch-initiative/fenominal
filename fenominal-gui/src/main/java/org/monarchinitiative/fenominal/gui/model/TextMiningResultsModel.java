package org.monarchinitiative.fenominal.gui.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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

}
