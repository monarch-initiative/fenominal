package org.monarchinitiative.fenominal.core.hpo;

import java.util.List;
import java.util.Optional;

public interface HpoConceptMapper {

    void addConcept(HpoConcept concept);

    Optional<HpoConceptHit> getMatch(List<String> words);

}
