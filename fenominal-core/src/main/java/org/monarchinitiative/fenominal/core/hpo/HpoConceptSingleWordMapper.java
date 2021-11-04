package org.monarchinitiative.fenominal.core.hpo;

import org.monarchinitiative.fenominal.core.FenominalRunTimeException;
import org.monarchinitiative.fenominal.core.lexical.LexicalResources;

import java.util.*;

public class HpoConceptSingleWordMapper implements HpoConceptMatch {

    private final Map<String, HpoConcept> componentWordToConceptMap;
    private final LexicalResources lexicalResources;

    public HpoConceptSingleWordMapper(LexicalResources lexicalResources) {
        componentWordToConceptMap = new HashMap<>();
        this.lexicalResources = lexicalResources;
    }

    public void addConcept(HpoConcept concept) {
        Set<String> concepts = concept.getNonStopWords();
        if (concepts.size() != 1) {
            throw new FenominalRunTimeException("Error, we were expected a concept with a single word but got "
                    + concept.getOriginalConcept());
        }
        this.componentWordToConceptMap.put(lexicalResources.getCluster(concept.getOriginalConcept().toLowerCase()), concept);
    }

    /**
     * Returns the first complete match.
     *
     * @param clusters list of lexical clusters mapped to the original text that have been preprocessed to remove stopwords
     * @return Optionally, the corresponding first match
     */
    @Override
    public Optional<HpoConcept> getMatch(List<String> clusters) {
        if (clusters.size() != 1) {
            throw new FenominalRunTimeException("Error, we were expecting a single word but got "
                    + String.join("-", clusters));
        }
        String cluster = clusters.get(0);

        if (this.componentWordToConceptMap.containsKey(cluster)) {
            return Optional.of(this.componentWordToConceptMap.get(cluster));
        } else {
            return Optional.empty();
        }
    }

}
