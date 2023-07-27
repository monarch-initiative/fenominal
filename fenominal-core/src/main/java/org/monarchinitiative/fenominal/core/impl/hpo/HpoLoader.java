package org.monarchinitiative.fenominal.core.impl.hpo;

import org.monarchinitiative.phenol.ontology.data.MinimalOntology;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Load HPO terms that descend from the Phenotypic Abnormality subontology
 */
public class HpoLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(HpoLoader.class);

    private static final TermId PHENOTYPIC_ABNORMALITY = TermId.of("HP:0000118");

    /**
     * A valid synonym needs to be at least 3 characters in length. This is needed because
     * the synonym "MI" for myocardial infarction is leading to false-positive hits.
     */
    private final static int LENGTH_THRESHOLD = 3;

    private final MinimalOntology hpo;

    /** There are some synonyms that make sense in the HPO but that in the context of text mining
     * commonly lead to false positive results. For instance, 'negative' is used to state that
     * a test was normal and should not be parsed as a synonym of Negativism (HP:0410291). We use
     * this set to omit them from parsing.
     */
    private final Set<String> SYNONYMS_TO_OMIT = Set.of("negative");

    public HpoLoader(MinimalOntology ontology) {
        this.hpo = ontology;
    }

    public synchronized Map<String, TermId> textToTermMap() {
        List<SimpleHpoTerm> terms = loadSimpleHpoTerms();
        return textToTermMap(terms);
    }

    public synchronized Map<String, TermId> textToTermMap(List<SimpleHpoTerm> termList) {
        Map<String, TermId> termmap = new HashMap<>();
        for (SimpleHpoTerm sht : termList) {
            TermId tid = sht.getId();
            termmap.put(sht.getName().toLowerCase(), tid);
            for (String synonym : sht.getSynonyms()) {
                if (synonym.length() < LENGTH_THRESHOLD) {
                    continue;
                } else if (SYNONYMS_TO_OMIT.contains(synonym)) {
                    continue;
                }
                termmap.put(synonym.toLowerCase(), tid);
            }
        }
        // Remove a synonym of Asthenia HP:0025406 that leads to FP results
        termmap.remove("weakness");
        return Map.copyOf(termmap); // make immutable
    }

    public synchronized List<SimpleHpoTerm> loadSimpleHpoTerms() {
        List<SimpleHpoTerm> termList = new ArrayList<>();

        for (TermId tid : hpo.nonObsoleteTermIds()) {
            if (hpo.graph().isAncestorOf(PHENOTYPIC_ABNORMALITY, tid)) {
                hpo.termForTermId(tid)
                        .map(SimpleHpoTerm::new)
                        .ifPresentOrElse(termList::add,
                                () -> LOGGER.warn("Term for {} not in ontology!", tid.getValue()));
            }
        }
        return List.copyOf(termList); // make immutable
    }
}
