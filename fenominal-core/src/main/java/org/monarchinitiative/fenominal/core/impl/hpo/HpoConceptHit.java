package org.monarchinitiative.fenominal.core.impl.hpo;

/**
 * An HpoConceptHit represents a match of an HPO concept with an actual mined text.
 * {@link #longestMatchingStretch()} returns the number of words in the longest stretch
 * of words matched in the right order.
 * {@link #correctMatch()} returns the proportions of letters that are correctly matched (by default 1, possibly
 * less with inexact matching).
 * @author Peter Robinson
 */
public interface HpoConceptHit {


    HpoConcept hpoConcept();
    default int longestMatchingStretch() { return 1;}
    default double correctMatch() { return 1.0d; }
}
