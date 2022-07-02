package org.monarchinitiative.fenominal.gui.model;

import java.util.List;

/**
 * Class representing a single medical encounter. For now, we record observed and excluded HPO terms.
 * We should extend this with a comprehensive way of entering PhenotypicFeatures.
 *
 * TODO Is this class still needed?
 *
 */
@Deprecated
public class MedicalEncounter {

    private final List<FenominalTerm> terms;

    MedicalEncounter(List<FenominalTerm> termList) {
        terms = List.copyOf(termList);
    }

    public List<FenominalTerm> getTerms() {
        return terms;
    }
}
