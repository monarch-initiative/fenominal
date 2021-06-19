package org.monarchinitiative.fenominal.core.hpo;

import java.util.List;
import java.util.Optional;

public interface HpoConceptMatch {

    void addConcept(HpoConcept concept);

    Optional<HpoConcept> getMatch(List<String> words);

}
