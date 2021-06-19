package org.monarchinitiative.fenominal.core.hpo;

import org.monarchinitiative.fenominal.core.FenominalRunTimeException;
import org.monarchinitiative.fenominal.core.lexical.LexicalClustersBuilder;

import java.util.*;

public class HpoConceptSingleWordMapper implements HpoConceptMatch {

    private final Map<String, HpoConcept> componentWordToConceptMap;
    private final LexicalClustersBuilder lexicalClustersBuilder;

    public HpoConceptSingleWordMapper(LexicalClustersBuilder lexicalClustersBuilder) {
        componentWordToConceptMap = new HashMap<>();
        this.lexicalClustersBuilder = lexicalClustersBuilder;
    }

    public void addConcept(HpoConcept concept) {
        Set<String> concepts = concept.getNonStopWords();
        if (concepts.size() != 1) {
            throw new FenominalRunTimeException("Error, we were expected a concept with a single word but got "
                    + concept.getOriginalConcept());
        }
        this.componentWordToConceptMap.put(lexicalClustersBuilder.getCluster(concept.getOriginalConcept().toLowerCase()), concept);
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
