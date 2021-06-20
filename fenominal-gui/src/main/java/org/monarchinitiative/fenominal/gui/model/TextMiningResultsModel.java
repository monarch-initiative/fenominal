package org.monarchinitiative.fenominal.gui.model;

import java.util.List;

public interface TextMiningResultsModel {
    void output();

    String getTsv();

    void addHpoFeatures(List<FenominalTerm> terms);

    int minedSoFar();

    int getTermCount();
}
