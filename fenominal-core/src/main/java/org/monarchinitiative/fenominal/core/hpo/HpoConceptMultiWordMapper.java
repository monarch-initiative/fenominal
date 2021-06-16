package org.monarchinitiative.fenominal.core.hpo;

import java.util.*;

/**
 * This app will use one object of this class for each wordcount. For instance, there will be
 * a separate mapper for concepts with one, two, three, etc words. This will let us perform
 * the heuristic searching for matches to multiword concepts a little more efficiently. Each
 * object contains all such concepts from the HPO and offers functions for searching.
 */
public class HpoConceptMultiWordMapper implements HpoConceptMatch {

    private final int n_words;
    /** The key is a word that appears in the HpoConcept. The value is a list of all concepts that
     * contain the word. Each concept is divided up into words like this for each of searching.
      */
    private final Map<String, List<HpoConcept>> componentWordToConceptMap;

    public HpoConceptMultiWordMapper(int n) {
        this.n_words = n;
        this.componentWordToConceptMap = new HashMap<>();
    }

    public void addConcept(HpoConcept concept) {
        for (String w : concept.getNonStopWords()) {
            this.componentWordToConceptMap.putIfAbsent(w, new ArrayList<>());
            this.componentWordToConceptMap.get(w).add(concept);
        }
    }

    /**
     * Returns the first complete match.
     * @param words list of words from the original text that have been preprocessed to remove stopwords
     * @return Optionally, the corresponding first match
     */
    public Optional<HpoConcept> getMatch(List<String> words) {
        Set<String> wordset = new HashSet<>(words);
        for (String word : words) {
            if (this.componentWordToConceptMap.containsKey(word)) {
                List<HpoConcept> conceptList = this.componentWordToConceptMap.get(word);
                for (HpoConcept hpoc : conceptList) {
                    if (hpoc.getNonStopWords().equals(wordset)) {
                        return Optional.of(hpoc);
                    }
                }
            }
        }
        return Optional.empty();
    }




}
