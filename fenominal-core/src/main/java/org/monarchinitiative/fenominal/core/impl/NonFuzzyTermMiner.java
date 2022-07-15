package org.monarchinitiative.fenominal.core.impl;

import org.monarchinitiative.fenominal.model.MinedSentence;
import org.monarchinitiative.fenominal.model.MinedTerm;
import org.monarchinitiative.fenominal.model.MinedTermWithMetadata;
import org.monarchinitiative.fenominal.model.impl.DefaultMinedTerm;
import org.monarchinitiative.fenominal.core.TermMiner;
import org.monarchinitiative.fenominal.model.impl.DetailedMinedTerm;
import org.monarchinitiative.fenominal.core.impl.lexical.LexicalResources;
import org.monarchinitiative.fenominal.core.impl.textmapper.ClinicalTextMapper;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class NonFuzzyTermMiner extends AbstractTermMiner implements TermMiner {
    private static final Logger LOGGER = LoggerFactory.getLogger(NonFuzzyTermMiner.class);
    private final ClinicalTextMapper mapper;

    public NonFuzzyTermMiner(Ontology ontology) {
        LexicalResources lexicalResources = new LexicalResources();
        this.mapper = new ClinicalTextMapper(ontology, lexicalResources);
    }


    /**
     * @param query Query string for mining HPO terms (for instance, text that was pasted into the GUI window for mining).
     * @return collection of mined HPO terms to display in the GUI
     */
    @Override
    public Collection<MinedTerm> mineTerms(final String query) {
        List<DetailedMinedTerm> mappedSentenceParts = mapper.mapText(query, false);
        LOGGER.trace("Retrieved {} mapped sentence parts.", mappedSentenceParts.size());
        return mappedSentenceParts.stream().map(DefaultMinedTerm::fromMappedSentencePart).collect(Collectors.toList());
    }

    @Override
    public Collection<MinedTermWithMetadata> mineTermsWithMetadata(String query) {
        List<DetailedMinedTerm> mappedSentenceParts = mapper.mapText(query, false);
        LOGGER.trace("Retrieved {} mapped sentence parts.", mappedSentenceParts.size());
        return Collections.unmodifiableList(mappedSentenceParts);
    }

    @Override
    public Collection<MinedSentence> mineSentences(String query) {
        List<MinedSentence> sentences = mapper.mapSentences(query, false);
        LOGGER.trace("Retrieved {} sentencess.", sentences.size());
        return sentences;
    }


}
