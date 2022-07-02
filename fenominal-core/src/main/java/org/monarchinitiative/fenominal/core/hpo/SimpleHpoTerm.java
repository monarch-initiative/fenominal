package org.monarchinitiative.fenominal.core.hpo;

import org.monarchinitiative.phenol.ontology.data.*;

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

    @Override
    public int hashCode() {
        // sufficient to ensure uniqueness
        return Objects.hash(this.id, this.name);
    }

    @Override
    public boolean equals(Object obj) {
        if (! (obj instanceof SimpleHpoTerm that)) return false;
        return this.id.equals(that.id) && this.name.equals(that.name);
    }

    @Override
    public String toString() {
        return "SimpleHpoTerm: " + this.name + "(" + this.id +")";
    }
}
