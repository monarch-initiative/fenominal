package org.monarchinitiative.fenominal.hpo;

import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.ontology.data.*;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class is a POJO that contains all relevant entries of an HPO term as Strings
 * that we will use for NER/NLP
 */
public class SimpleHpoTerm {
    private final TermId id;
    private final  String name;
    private final  String def;
    private final  Set<String> xrefs;
    private final  Set<String> synonyms;

    public SimpleHpoTerm(Term term) {
        this.id = term.getId();
        this.name = term.getName();
        this.def = term.getDefinition();
        xrefs = term.getXrefs().stream().map(Dbxref::getName).collect(Collectors.toSet());
        synonyms = term.getSynonyms().stream().map(TermSynonym::getValue).collect(Collectors.toSet());
    }

    public TermId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDef() {
        return def;
    }

    public Set<String> getXrefs() {
        return xrefs;
    }

    public Set<String> getSynonyms() {
        return synonyms;
    }


    public static synchronized List<SimpleHpoTerm> loadSimpleHpoTerms(String hpoOboPath) {
        return loadSimpleHpoTerms(new File((hpoOboPath)));
    }


    public static synchronized Map<String, TermId> textToTermMap(String pathToHpObo) {
        List<SimpleHpoTerm> terms = loadSimpleHpoTerms(new File(pathToHpObo));
        return textToTermMap(terms);
    }

    public static synchronized Map<String, TermId> textToTermMap(List<SimpleHpoTerm> termList) {
        Map<String, TermId> termmap = new HashMap<>();
        for (SimpleHpoTerm sht : termList) {
            TermId tid = sht.getId();
            termmap.put(sht.name.toLowerCase(), tid);
            for (String synonym : sht.getSynonyms()) {
                termmap.put(synonym.toLowerCase(), tid);
            }
        }
        return Map.copyOf(termmap); // make immutable
    }

    public static synchronized List<SimpleHpoTerm> loadSimpleHpoTerms(File hpoOboFile) {
        Ontology ontology = OntologyLoader.loadOntology(hpoOboFile);
        List<SimpleHpoTerm> termList = new ArrayList<>();
        for (TermId tid : ontology.getNonObsoleteTermIds()) {
            Term term = ontology.getTermMap().get(tid);
            termList.add(new SimpleHpoTerm(term));
        }
        return List.copyOf(termList); // make immutable
    }
}
