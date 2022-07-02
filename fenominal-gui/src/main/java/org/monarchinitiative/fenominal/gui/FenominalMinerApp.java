package org.monarchinitiative.fenominal.gui;

import org.monarchinitiative.fenominal.core.MinedTerm;
import org.monarchinitiative.fenominal.core.TermMiner;
import org.monarchinitiative.fenominal.core.corenlp.MappedSentencePart;
import org.monarchinitiative.fenominal.core.lexical.LexicalResources;
import org.monarchinitiative.fenominal.core.textmapper.ClinicalTextMapper;
import org.monarchinitiative.fenominal.core.SimpleMinedTerm;


import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class FenominalMinerApp implements TermMiner {

    private static final Logger LOGGER = LoggerFactory.getLogger(FenominalMinerApp.class);
    private final ClinicalTextMapper mapper;

    private final Ontology ontology;

    public FenominalMinerApp(Ontology ontology) {
        this.ontology = ontology;
        LexicalResources lexicalResources = new LexicalResources();
        this.mapper = new ClinicalTextMapper(ontology, lexicalResources);
    }

    /**
     * @param query Query string for mining HPO terms (for instance, text that was pasted into the GUI window for mining).
     * @return collection of mined HPO terms to display in the GUI
     */
    @Override
    public Collection<MinedTerm> doMining(final String query) {
        List<MappedSentencePart> mappedSentenceParts = mapper.mapText(query, false);
        LOGGER.trace("Retrieved {} mapped sentence parts ", mappedSentenceParts.size());
        return mappedSentenceParts.stream().map(SimpleMinedTerm::fromMappedSentencePart).collect(Collectors.toList());
    }

    @Override
    public Collection<MinedTerm> doFuzzyMining(final String query) {
        List<MappedSentencePart> mappedSentenceParts = mapper.mapText(query, true);
        LOGGER.trace("(Fuzzy match) Retrieved {} mapped sentence parts ", mappedSentenceParts.size());
        return mappedSentenceParts.stream().map(SimpleMinedTerm::fromMappedSentencePart).collect(Collectors.toList());
    }


    public Ontology getHpo() {
        return this.ontology;
    }


}
