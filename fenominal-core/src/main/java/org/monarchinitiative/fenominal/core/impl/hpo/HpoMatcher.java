package org.monarchinitiative.fenominal.core.impl.hpo;

import java.util.List;
import java.util.Optional;

public interface HpoMatcher {
    Optional<HpoConceptHit> getMatch(List<String> words);
}
