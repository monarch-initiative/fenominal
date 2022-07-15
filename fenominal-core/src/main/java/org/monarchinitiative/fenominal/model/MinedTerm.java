package org.monarchinitiative.fenominal.model;

import org.monarchinitiative.fenominal.model.impl.DefaultMinedTerm;

public interface MinedTerm {

    static MinedTerm of(int begin, int end, String termId, boolean isPresent) {
        return new DefaultMinedTerm(begin, end, termId, isPresent);
    }

    /**
     * @return zero-based begin coordinate (not included) of text region, based on which this <code>term</code> was identified.
     * The coordinate is with respect to the whole query text that has been mined for <code>term</code>s
     */
    int getBegin();

    /**
     * @return zero-based end coordinate (included) of text region, based on which this <code>term</code> was identified.
     * The coordinate is with respect to the whole query text that has been mined for <code>term</code>s
     */
    int getEnd();

    /**
     * @return String with ontology term id identified in the text region (e.g. <code>HP:123456</code>, <code>MP:654321</code>)
     */
    String getTermIdAsString();

    /**
     * @return <code>true</code>, if the <code>term</code> is present in the patient's phenotype
     */
    boolean isPresent();
}
