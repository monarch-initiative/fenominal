package org.monarchinitiative.fenominal.gui;

import org.monarchinitiative.fenominal.core.corenlp.MappedSentencePart;
import org.monarchinitiative.fenominal.core.lexical.LexicalClustersBuilder;
import org.monarchinitiative.fenominal.core.textmapper.ClinicalTextMapper;
import org.monarchinitiative.hpotextmining.core.miners.MinedTerm;
import org.monarchinitiative.hpotextmining.core.miners.TermMiner;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class FenominalMiner implements TermMiner {

    private static final Logger LOGGER = LoggerFactory.getLogger(FenominalMiner.class);
    private final ClinicalTextMapper mapper;
    private final LexicalClustersBuilder lexicalClustersBuilder;

    public FenominalMiner(Ontology ontology) {
        this.lexicalClustersBuilder = new LexicalClustersBuilder();
        this.mapper = new ClinicalTextMapper(ontology, lexicalClustersBuilder);
    }

    /**
     * @param query Query string for mining HPO terms (for instance, text that was pasted into the GUI window for mining).
     * @return collection of mined HPO terms to display in the GUI
     */
    @Override
    public Collection<MinedTerm> doMining(final String query) {
        List<MappedSentencePart> mappedSentenceParts = mapper.mapText(query);
        LOGGER.trace("Retrieved {} mapped sentence parts ", mappedSentenceParts.size());
        return mappedSentenceParts.stream().map(SimpleMinedTerm::fromMappedSentencePart).collect(Collectors.toList());
    }

    public Ontology getHpo() {
        return this.mapper.getHpo();
    }


}
