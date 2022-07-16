package org.monarchinitiative.fenominal.core.impl.hpo;

import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO - the ontology loader belongs elsewhere, perhaps to IO, or CLI/GUI module, so that we can keep `fenominal-core`
//  to depend only on `phenol-core`
public class HpoLoader {

    private final Ontology hpo;

    public HpoLoader(String pathToHpJson) {
        this.hpo = OntologyLoader.loadOntology(new File(pathToHpJson));
    }

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
            Term term = this.hpo.getTermMap().get(tid);
            termList.add(new SimpleHpoTerm(term));
        }
        return List.copyOf(termList); // make immutable
    }
}
