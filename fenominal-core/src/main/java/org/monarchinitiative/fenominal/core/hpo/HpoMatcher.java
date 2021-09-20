package org.monarchinitiative.fenominal.core.hpo;

import org.monarchinitiative.fenominal.core.lexical.LexicalClustersBuilder;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class HpoMatcher {
    private static final Logger LOGGER = LoggerFactory.getLogger(HpoMatcher.class);

    private final LexicalClustersBuilder lexicalClustersBuilder;
    private final Map<Integer, HpoConceptMatch> wordCountToMatcherMap;

    private final static TermId PHENOTYPIC_ABN = TermId.of("HP:0000118");
    private final static TermId CLINICAL_MODIFIER = TermId.of("HP:0012823");
    /** Note that Clinical course is a subterm of Clinical modifier. */
    private final static TermId CLINICAL_COURSE = TermId.of("HP:0031797");

    private final Ontology phenotypicAbnormality;
    private final Ontology clinicalModifierOntology;
    private final Ontology clinicalCourseOntology;



    public HpoMatcher(Ontology ontology, LexicalClustersBuilder lexicalClustersBuilder) {
        this.phenotypicAbnormality = ontology.subOntology(PHENOTYPIC_ABN);
        this.clinicalModifierOntology = ontology.subOntology(CLINICAL_MODIFIER);
        this.clinicalCourseOntology = ontology.subOntology(CLINICAL_COURSE);
        this.lexicalClustersBuilder = lexicalClustersBuilder;
        HpoLoader loader = new HpoLoader(ontology);
        Map<String, TermId> textToTermMap = loader.textToTermMap();
        this.wordCountToMatcherMap = new HashMap<>();
        this.wordCountToMatcherMap.put(1, new HpoConceptSingleWordMapper(lexicalClustersBuilder));
        this.wordCountToMatcherMap.put(2, new HpoConceptMultiWordMapper(2, lexicalClustersBuilder));
        this.wordCountToMatcherMap.put(3, new HpoConceptMultiWordMapper(3, lexicalClustersBuilder));
        this.wordCountToMatcherMap.put(4, new HpoConceptMultiWordMapper(4, lexicalClustersBuilder));
        this.wordCountToMatcherMap.put(5, new HpoConceptMultiWordMapper(5, lexicalClustersBuilder));
        this.wordCountToMatcherMap.put(6, new HpoConceptMultiWordMapper(6, lexicalClustersBuilder));
        this.wordCountToMatcherMap.put(7, new HpoConceptMultiWordMapper(7, lexicalClustersBuilder));
        this.wordCountToMatcherMap.put(8, new HpoConceptMultiWordMapper(8, lexicalClustersBuilder));
        this.wordCountToMatcherMap.put(9, new HpoConceptMultiWordMapper(9, lexicalClustersBuilder));
        this.wordCountToMatcherMap.put(10, new HpoConceptMultiWordMapper(10, lexicalClustersBuilder));
        this.wordCountToMatcherMap.put(11, new HpoConceptMultiWordMapper(11, lexicalClustersBuilder));
        this.wordCountToMatcherMap.put(12, new HpoConceptMultiWordMapper(12, lexicalClustersBuilder));
        this.wordCountToMatcherMap.put(13, new HpoConceptMultiWordMapper(13, lexicalClustersBuilder));
        this.wordCountToMatcherMap.put(14, new HpoConceptMultiWordMapper(14, lexicalClustersBuilder));
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

    public Optional<HpoConcept> getMatch(List<String> words) {
        if (words.size() > 14) {
            LOGGER.error("Maximum current word count is 14 but we got " +
                    words.size() + " for \"" + words + "\"");
        } else if (words.isEmpty()) {
            LOGGER.error("Empty word list passed");
        } else {
            HpoConceptMatch matcher = this.wordCountToMatcherMap.get(words.size());
            List<String> clusters = lexicalClustersBuilder.getClusters(words);
            return matcher.getMatch(clusters);
        }
        return Optional.empty();
    }

    public Ontology getHpoPhenotypicAbnormality() {
        return this.phenotypicAbnormality;
    }
}
