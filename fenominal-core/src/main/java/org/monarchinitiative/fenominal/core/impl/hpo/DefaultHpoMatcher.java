package org.monarchinitiative.fenominal.core.impl.hpo;

import org.monarchinitiative.fenominal.core.impl.lexical.LexicalResources;
import org.monarchinitiative.phenol.ontology.data.MinimalOntology;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DefaultHpoMatcher implements HpoMatcher {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultHpoMatcher.class);

    private final LexicalResources lexicalResources;
    private final Map<Integer, HpoConceptMapper> wordCountToMatcherMap;

    public DefaultHpoMatcher(MinimalOntology ontology, LexicalResources lexicalResources) {
        this.lexicalResources = lexicalResources;
        HpoLoader loader = new HpoLoader(ontology);
        Map<String, TermId> textToTermMap = loader.textToTermMap();
        this.wordCountToMatcherMap = new HashMap<>();
        this.wordCountToMatcherMap.put(1, new HpoConceptSingleWordMapper(lexicalResources));
        this.wordCountToMatcherMap.put(2, new HpoConceptMultiWordMapper(2, lexicalResources));
        this.wordCountToMatcherMap.put(3, new HpoConceptMultiWordMapper(3, lexicalResources));
        this.wordCountToMatcherMap.put(4, new HpoConceptMultiWordMapper(4, lexicalResources));
        this.wordCountToMatcherMap.put(5, new HpoConceptMultiWordMapper(5, lexicalResources));
        this.wordCountToMatcherMap.put(6, new HpoConceptMultiWordMapper(6, lexicalResources));
        this.wordCountToMatcherMap.put(7, new HpoConceptMultiWordMapper(7, lexicalResources));
        this.wordCountToMatcherMap.put(8, new HpoConceptMultiWordMapper(8, lexicalResources));
        this.wordCountToMatcherMap.put(9, new HpoConceptMultiWordMapper(9, lexicalResources));
        this.wordCountToMatcherMap.put(10, new HpoConceptMultiWordMapper(10, lexicalResources));
        this.wordCountToMatcherMap.put(11, new HpoConceptMultiWordMapper(11, lexicalResources));
        this.wordCountToMatcherMap.put(12, new HpoConceptMultiWordMapper(12, lexicalResources));
        this.wordCountToMatcherMap.put(13, new HpoConceptMultiWordMapper(13, lexicalResources));
        this.wordCountToMatcherMap.put(14, new HpoConceptMultiWordMapper(14, lexicalResources));
        for (var e : textToTermMap.entrySet()) {
            HpoConcept concept = new HpoConcept(e.getKey(), e.getValue());
            // put concept into the correct Map depending on how many non-stop words it has
            int n_words = concept.wordCount();
            if (n_words > 14) {
                LOGGER.error("Maximum current word count is 14 but we got " +
                        n_words + " for \"" + concept.getOriginalConcept() + "\"");
                continue;
            }
            this.wordCountToMatcherMap.get(n_words).addConcept(concept);
        }
    }

    @Override
    public Optional<HpoConceptHit> getMatch(List<String> words) {
        if (words.size() > 14) {
            LOGGER.error("Maximum current word count is 14 but we got " +
                    words.size() + " for \"" + words + "\"");
        } else if (words.isEmpty()) {
            LOGGER.error("Empty word list passed");
        } else {
            HpoConceptMapper matcher = this.wordCountToMatcherMap.get(words.size());
            List<String> clusters = lexicalResources.getClusters(words);
            return matcher.getMatch(clusters);
        }
        return Optional.empty();
    }

}
