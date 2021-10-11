package org.monarchinitiative.fenominal.gui.model;

import java.util.List;

/**
 * Class representing a single medical encounter. For now, we record observed and excluded HPO terms.
 * We should extend this with a comprehensive way of entering PhenotypicFeatures. This class could be
 * partially merged with {@link CaseReport}
 *
 */
public class MedicalEncounter {

    private List<FenominalTerm> terms;

    MedicalEncounter(List<FenominalTerm> termList) {
        terms = List.copyOf(termList);
    }
}
