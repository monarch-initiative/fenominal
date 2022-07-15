package org.monarchinitiative.fenominal.core;


import org.monarchinitiative.fenominal.core.impl.FuzzyTermMiner;
import org.monarchinitiative.fenominal.core.impl.NonFuzzyTermMiner;
import org.monarchinitiative.fenominal.model.MinedTerm;
import org.monarchinitiative.fenominal.model.MinedTermWithMetadata;
import org.monarchinitiative.fenominal.model.impl.DefaultMinedTerm;
import org.monarchinitiative.phenol.ontology.data.Ontology;

import java.io.File;
import java.util.Collection;
import java.util.Set;

/**
 * Classes implementing this interface are able to mine a set of {@link MinedTerm}s from given <code>query</code>.
 * <p>
 * The query is a free text, usually containing a description of a clinical situation of the patient/proband. The aim of
 * the text-mining is to identify a set of HPO terms that represent patient's phenotype. Terms may be either present
 * or absent (see {@link MinedTerm} for more info). Set of identified terms is presented to the user/curator for
 * further approval/rejection.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.2.1
 * @since 0.2
 */
public interface TermMiner {

    /**
     * Parse given <code>query</code> String and return set of {@link DefaultMinedTerm}s representing HPO terms identified
     * in the <code>query</code>.
     * <p>
     * The <em>mining</em> process might be blocking so it would be nice to perform the mining on another thread than the
     * event loop thread of a Gui.
     */

    static TermMiner defaultNonFuzzyMapper(Ontology ontology) {
        return new NonFuzzyTermMiner(ontology);
    }

    static TermMiner defaultFuzzyMapper(Ontology ontology) {
        return new FuzzyTermMiner(ontology);
    }

    Collection<MinedTerm> doMining(final String query);

    Collection<MinedTermWithMetadata> doMiningWithMetadata(final String query);

    /**
     * Not sure if we want to just always do this on the fly, but let's expose this for test for right now
     * @param ontology copy of HPO
     * @param file write the serialized kmer data to this file
     * @param k the length of the kmers to index
     */
    void serializeKmersToFile(Ontology ontology, File file, int k);

}
