package org.monarchinitiative.fenominal;

import org.monarchinitiative.fenominal.model.MinedTerm;
import org.monarchinitiative.fenominal.model.SimpleMinedTerm;
import org.monarchinitiative.fenominal.core.TermMiner;
import org.monarchinitiative.fenominal.model.MappedSentencePart;
import org.monarchinitiative.fenominal.core.lexical.LexicalResources;
import org.monarchinitiative.fenominal.core.textmapper.ClinicalTextMapper;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class FenominalTermMiner implements TermMiner {
    private static final Logger LOGGER = LoggerFactory.getLogger(FenominalTermMiner.class);
    private final ClinicalTextMapper mapper;

    public FenominalTermMiner(Ontology ontology) {
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
        LOGGER.trace("Retrieved {} mapped sentence parts.", mappedSentenceParts.size());
        return mappedSentenceParts.stream().map(SimpleMinedTerm::fromMappedSentencePart).collect(Collectors.toList());
    }

    /**
     * Do text mining with kmer-fuzzy match algorithm
     * @param query Query string for mining HPO terms (for instance, text that was pasted into the GUI window for mining).
     * @return collection of mined HPO terms to display in the GUI
     */
    @Override
    public Collection<MinedTerm> doFuzzyMining(final String query) {
        List<MappedSentencePart> mappedSentenceParts = mapper.mapText(query, true);
        LOGGER.trace("Retrieved {} mapped sentence parts (fuzzy (true).", mappedSentenceParts.size());
        return mappedSentenceParts.stream().map(SimpleMinedTerm::fromMappedSentencePart).collect(Collectors.toList());
    }
}
