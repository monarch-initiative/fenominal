package org.monarchinitiative.fenominal.hpo;


import org.monarchinitiative.fenominal.corenlp.StopWords;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.HashSet;
import java.util.Set;

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
        this.nonStopWords = new HashSet<>();
        for (var w : words) {
            if (StopWords.isStop(w)) {
                continue;
            } else {
                this.nonStopWords.add(w);
            }
        }
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
