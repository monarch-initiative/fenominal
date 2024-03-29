package org.monarchinitiative.fenominal.core.impl;

import org.monarchinitiative.fenominal.core.TermMiner;
import org.monarchinitiative.fenominal.model.MinedSentence;
import org.monarchinitiative.fenominal.model.MinedTerm;
import org.monarchinitiative.fenominal.model.MinedTermWithMetadata;
import org.monarchinitiative.fenominal.model.impl.DefaultMinedTerm;
import org.monarchinitiative.fenominal.model.impl.DetailedMinedTerm;
import org.monarchinitiative.fenominal.core.impl.lexical.LexicalResources;
import org.monarchinitiative.fenominal.core.impl.textmapper.ClinicalTextMapper;
import org.monarchinitiative.phenol.ontology.data.MinimalOntology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class performs fuzzy matching
 */
public class FuzzyTermMiner implements TermMiner {
    private static final Logger LOGGER = LoggerFactory.getLogger(FuzzyTermMiner.class);
    private final ClinicalTextMapper hpoMapper;

    private final LexicalResources lexicalResources;

    public FuzzyTermMiner(MinimalOntology ontology) {
        lexicalResources = new LexicalResources();
        hpoMapper = new ClinicalTextMapper(ontology, lexicalResources);
    }

    /**
     * Do text mining with kmer-fuzzy match algorithm
     * @param query Query string for mining HPO terms (for instance, text that was pasted into the GUI window for mining).
     * @return collection of mined HPO terms to display in the GUI
     */
    @Override
    public Collection<MinedTerm> mineTerms(String query) {
        List<DetailedMinedTerm> mappedSentenceParts = hpoMapper.mapText(query, true);
        LOGGER.trace("Retrieved {} MinedTerms (fuzzy).", mappedSentenceParts.size());
        return mappedSentenceParts.stream().map(DefaultMinedTerm::fromMappedSentencePart).collect(Collectors.toList());
    }



    /**
     * Do text mining with kmer-fuzzy match algorithm
     * @param query Query string for mining HPO terms (for instance, text that was pasted into the GUI window for mining).
     * @return collection of mined HPO terms with metadata to display in the GUI
     */
    @Override
    public Collection<MinedTermWithMetadata> mineTermsWithMetadata(String query) {
        List<DetailedMinedTerm> mappedSentenceParts = hpoMapper.mapText(query, true);
        LOGGER.trace("Retrieved {} MinedTermWithMetadata objects (fuzzy).", mappedSentenceParts.size());
        return Collections.unmodifiableList(mappedSentenceParts);
    }

    @Override
    public Collection<MinedSentence> mineSentences(String query) {
        List<MinedSentence> sentences = hpoMapper.mapSentences(query, true);
        LOGGER.trace("Retrieved {} sentencess.", sentences.size());
        return sentences;
    }


}
