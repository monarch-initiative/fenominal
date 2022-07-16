package org.monarchinitiative.fenominal.core.impl.hpo;

/**
 * This is the object that represents an actual match of an HPO concept with a part of the text and includes
 * the length of the longest stretch of words matched in the original order.
 * @author Peter Robinson
 */
public class DefaultHpoConceptHit implements HpoConceptHit {

    private final HpoConcept hpoConcept;
    private final int longestMatchingStretch;


public DefaultHpoConceptHit(HpoConcept hpoConcept, int longestMatch) {
    this.hpoConcept = hpoConcept;
    this.longestMatchingStretch = longestMatch;
}
    public DefaultHpoConceptHit(HpoConcept hpoConcept) {
        this.hpoConcept = hpoConcept;
        this.longestMatchingStretch = 1;
    }

    @Override
    public int longestMatchingStretch() {
        return longestMatchingStretch;
    }

    @Override
    public HpoConcept hpoConcept() {
        return hpoConcept;
    }
}
