package org.monarchinitiative.fenominal.core.hpo;

import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class HpoMatcher {


    private final Map<Integer, HpoConceptMatch> wordCountToMatcherMap;
    private final Ontology hpo;


    public HpoMatcher(String pathToHpObo) {
        HpoLoader loader = new HpoLoader(pathToHpObo);
        this.hpo = loader.getHpo();
        Map<String, TermId> textToTermMap = loader.textToTermMap();
        this.wordCountToMatcherMap = new HashMap<>();
        this.wordCountToMatcherMap.put(1, new HpoConceptSingleWordMapper());
        this.wordCountToMatcherMap.put(2, new HpoConceptMultiWordMapper(2));
        this.wordCountToMatcherMap.put(3, new HpoConceptMultiWordMapper(3));
        this.wordCountToMatcherMap.put(4, new HpoConceptMultiWordMapper(4));
        this.wordCountToMatcherMap.put(5, new HpoConceptMultiWordMapper(5));
        this.wordCountToMatcherMap.put(6, new HpoConceptMultiWordMapper(6));
        this.wordCountToMatcherMap.put(7, new HpoConceptMultiWordMapper(7));
        this.wordCountToMatcherMap.put(8, new HpoConceptMultiWordMapper(8));
        this.wordCountToMatcherMap.put(9, new HpoConceptMultiWordMapper(9));
        this.wordCountToMatcherMap.put(10, new HpoConceptMultiWordMapper(10));
        this.wordCountToMatcherMap.put(11, new HpoConceptMultiWordMapper(11));
        this.wordCountToMatcherMap.put(12, new HpoConceptMultiWordMapper(12));
        this.wordCountToMatcherMap.put(13, new HpoConceptMultiWordMapper(13));
        this.wordCountToMatcherMap.put(14, new HpoConceptMultiWordMapper(14));
        for (var e : textToTermMap.entrySet()) {
            HpoConcept concept = new HpoConcept(e.getKey(), e.getValue());
            // put concept into the correct Map depending on how many non-stop words it has
            int n_words = concept.wordCount();
            if (n_words>14) {
                throw new FenominalRunTimeException("Maximum current word count is 14 but we got " +
                        n_words + " for \"" + concept.getOriginalConcept() + "\"");
            }
            this.wordCountToMatcherMap.get(n_words).addConcept(concept);
        }
    }

    public Optional<HpoConcept> getMatch(List<String> words) {
        if (words.size() > 14) {
            System.err.println("Maximum current word count is 14 but we got " +
                    words.size() + " for \"" + words + "\"");
        }
        return Optional.empty();
    }

    public Ontology getHpo() {
        return hpo;
    }
}