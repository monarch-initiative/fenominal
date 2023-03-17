package org.monarchinitiative.fenominal.core.impl.hpo;

import org.monarchinitiative.phenol.ontology.algo.OntologyAlgorithm;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.*;

/**
 * Load HPO terms that descend from the Phenotypic Abnormality subontology
 */
public class HpoLoader {


    private static final TermId PHENOTYPIC_ABNORMALITY = TermId.of("HP:0000118");

    private final Ontology hpo;

    /** There are some synonyms that make sense in the HPO but that in the context of text mining
     * commonly lead to false positive results. For instance, 'negative' is used to state that
     * a test was normal and should not be parsed as a synonym of Negativism (HP:0410291). We use
     * this set to omit them from parsing.
     */
    private final Set<String> SYNONYMS_TO_OMIT = Set.of("negative");

    public HpoLoader(Ontology ontology) {
        this.hpo = ontology;
    }

    public Ontology getHpo() {
        return hpo;
    }

    /**
     * A valid synonym needs to be at least 3 characters in length. This is needed because
     * the synonym "MI" for myocardial infarction is leading to false-positive hits.
     */
    private final static int LENGTH_THRESHOLD = 3;

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
        for (TermId tid : this.hpo.getNonObsoleteTermIds()) {
            if (OntologyAlgorithm.isSubclass(this.hpo, tid, PHENOTYPIC_ABNORMALITY)) {
                Term term = this.hpo.getTermMap().get(tid);
                termList.add(new SimpleHpoTerm(term));
            }
        }
        return List.copyOf(termList); // make immutable
    }
}
