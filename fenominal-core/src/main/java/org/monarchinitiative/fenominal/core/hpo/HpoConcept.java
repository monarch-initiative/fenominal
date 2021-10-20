package org.monarchinitiative.fenominal.core.hpo;


import org.monarchinitiative.fenominal.core.corenlp.StopWords;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * This class contains one HPO label or synonym and provides functions for
 * searching for matches in input texts. For instance, we store all of the non-stop words in a set and can
 * check not only for exact matches with the label etc but permutations thereof.
 */
public class HpoConcept {

    private final String originalConcept;
    private final Set<String> nonStopWords;
    private final TermId hpoId;

    public HpoConcept(String concept, TermId tid) {
        this.originalConcept = concept;
        this.hpoId = tid;
        // Note that because of the Q/C for HPO, there are only single spaces and no other kind of whitespace
        // in labels or synonyms
        String [] words = concept.split(" ");
        this.nonStopWords = Arrays.stream(words)
                .filter(Predicate.not(StopWords::isStop))
                .collect(Collectors.toSet());
    }

    public String getOriginalConcept() {
        return originalConcept;
    }

    public Set<String> getNonStopWords() {
        return nonStopWords;
    }

    public TermId getHpoId() {
        return hpoId;
    }

    public int wordCount() {
        return this.nonStopWords.size();
    }
}
