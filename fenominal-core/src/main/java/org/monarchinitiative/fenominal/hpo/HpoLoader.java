package org.monarchinitiative.fenominal.hpo;

import org.monarchinitiative.fenominal.json.JsonHpoParser;
import org.monarchinitiative.fenominal.json.OntologyLoader;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HpoLoader {

    private final Ontology hpo;

    public HpoLoader(String pathToHpJson) {
        JsonHpoParser parser = new JsonHpoParser(pathToHpJson);
        this.hpo = parser.getHpo();
    }

    public Ontology getHpo() {
        return hpo;
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
                termmap.put(synonym.toLowerCase(), tid);
            }
        }
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
