package org.monarchinitiative.fenominal.core.hpo;

import org.monarchinitiative.fenominal.core.lexical.LexicalClustersBuilder;

import java.util.*;

/**
 * This app will use one object of this class for each wordcount. For instance, there will be
 * a separate mapper for concepts with one, two, three, etc words. This will let us perform
 * the heuristic searching for matches to multiword concepts a little more efficiently. Each
 * object contains all such concepts from the HPO and offers functions for searching.
 */
public class HpoConceptMultiWordMapper implements HpoConceptMatch {

    private final int n_words;
    /**
     * The key is a word that appears in the HpoConcept. The value is a list of all concepts that
     * contain the word. Each concept is divided up into words like this for each of searching.
     */
    private final Map<String, List<HpoConcept>> componentWordToConceptMap;
    private final LexicalClustersBuilder lexicalClustersBuilder;

    public HpoConceptMultiWordMapper(int n, LexicalClustersBuilder lexicalClustersBuilder) {
        this.n_words = n;
        this.lexicalClustersBuilder = lexicalClustersBuilder;
        this.componentWordToConceptMap = new HashMap<>();
    }

    public void addConcept(HpoConcept concept) {
        for (String w : concept.getNonStopWords()) {
            String cluster = lexicalClustersBuilder.getCluster(w.toLowerCase());
            this.componentWordToConceptMap.putIfAbsent(cluster, new ArrayList<>());
            this.componentWordToConceptMap.get(cluster).add(concept);
        }
    }

    /**
     * Returns the first complete match.
     *
     * @param clusters list of lexical clusters mapped to the original text that have been preprocessed to remove stopwords
     * @return Optionally, the corresponding first match
     */
    public Optional<HpoConcept> getMatch(List<String> clusters) {
        Set<String> clusterSet = new HashSet<>(clusters);
        for (String cluster : clusters) {
            if (this.componentWordToConceptMap.containsKey(cluster)) {
                List<HpoConcept> conceptList = this.componentWordToConceptMap.get(cluster);
                for (HpoConcept hpoc : conceptList) {
                    if (lexicalClustersBuilder.getClusters(hpoc.getNonStopWords()).equals(clusterSet)) {
                        return Optional.of(hpoc);
                    }
                }
            }
        }
        return Optional.empty();
    }

    public int getN_words() {
        return n_words;
    }
}
