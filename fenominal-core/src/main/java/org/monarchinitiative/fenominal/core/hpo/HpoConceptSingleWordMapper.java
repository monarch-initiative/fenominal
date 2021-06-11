package org.monarchinitiative.fenominal.core.hpo;

import org.monarchinitiative.fenominal.core.except.FenominalRunTimeException;

import java.util.*;

public class HpoConceptSingleWordMapper implements HpoConceptMatch {

    private final Map<String, HpoConcept> componentWordToConceptMap;

    public HpoConceptSingleWordMapper() {
        componentWordToConceptMap = new HashMap<>();
    }

    public void addConcept(HpoConcept concept) {
        Set<String> concepts = concept.getNonStopWords();
        if (concepts.size() != 1) {
            throw new FenominalRunTimeException("Error, we were expected a concept with a single word but got "
                    + concept.getOriginalConcept());
        }
        this.componentWordToConceptMap.put(concept.getOriginalConcept(), concept);
    }

    /**
     * Returns the first complete match.
     * @param words list of words from the original text that have been preprocessed to remove stopwords
     * @return Optionally, the corresponding first match
     */
    @Override
    public Optional<HpoConcept> getMatch(List<String> words) {
       if (words.size() != 1) {
           throw new FenominalRunTimeException("Error, we were expecting a single word but got "
                   + String.join("-", words));
       }
       String word = words.get(0);

       if (this.componentWordToConceptMap.containsKey(word)) {
           return Optional.of(this.componentWordToConceptMap.get(word));
       } else {
           return Optional.empty();
       }
    }

}
