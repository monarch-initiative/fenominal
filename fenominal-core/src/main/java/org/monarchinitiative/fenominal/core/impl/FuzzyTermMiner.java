package org.monarchinitiative.fenominal.core.impl;

import org.monarchinitiative.fenominal.core.TermMiner;
import org.monarchinitiative.fenominal.model.MinedTerm;
import org.monarchinitiative.fenominal.model.MinedTermWithMetadata;
import org.monarchinitiative.fenominal.model.impl.DefaultMinedTerm;
import org.monarchinitiative.fenominal.model.impl.DetailedMinedTerm;
import org.monarchinitiative.fenominal.core.impl.lexical.LexicalResources;
import org.monarchinitiative.fenominal.core.impl.textmapper.ClinicalTextMapper;
import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class performs fuzzy matching
 * // TODO Tudor -- do something like this with the new algorithm
 */
public class FuzzyTermMiner extends AbstractTermMiner implements TermMiner {
    private static final Logger LOGGER = LoggerFactory.getLogger(FuzzyTermMiner.class);
    private final ClinicalTextMapper hpoMatcher;

    private final LexicalResources lexicalResources;

    public FuzzyTermMiner(String pathToHpJson) {
        Ontology hpo = OntologyLoader.loadOntology(new File(pathToHpJson));
        lexicalResources = new LexicalResources();
        hpoMatcher = new ClinicalTextMapper(hpo, lexicalResources);
    }

    public FuzzyTermMiner(Ontology ontology) {
        lexicalResources = new LexicalResources();
        hpoMatcher = new ClinicalTextMapper(ontology, lexicalResources);
    }

    public synchronized List<DetailedMinedTerm> mapText(String text) {
        // TODO: Decide where to put the fuzzy flag !!!
        return hpoMatcher.mapText(text, true);
    }

    public static void main(String[] args) {
        FuzzyTermMiner textToHpoMapper = new FuzzyTermMiner("/home/tudor/dev/fenominal/fenominal-core/src/test/resources/hpo/hp.json");
        textToHpoMapper.mapText("Short finger and toes with trident hands, macrocephaly with prominent forehead frontal bossing.");
    }
    /**
     * Do text mining with kmer-fuzzy match algorithm
     * @param query Query string for mining HPO terms (for instance, text that was pasted into the GUI window for mining).
     * @return collection of mined HPO terms to display in the GUI
     */
    @Override
    public Collection<MinedTerm> doMining(String query) {
        List<DetailedMinedTerm> mappedSentenceParts = hpoMatcher.mapText(query, true);
        LOGGER.trace("Retrieved {} MinedTerms (fuzzy).", mappedSentenceParts.size());
        return mappedSentenceParts.stream().map(DefaultMinedTerm::fromMappedSentencePart).collect(Collectors.toList());
    }



    /**
     * Do text mining with kmer-fuzzy match algorithm
     * @param query Query string for mining HPO terms (for instance, text that was pasted into the GUI window for mining).
     * @return collection of mined HPO terms with metadata to display in the GUI
     */
    @Override
    public Collection<MinedTermWithMetadata> doMiningWithMetadata(String query) {
        List<DetailedMinedTerm> mappedSentenceParts = hpoMatcher.mapText(query, true);
        LOGGER.trace("Retrieved {} MinedTermWithMetadata objects (fuzzy).", mappedSentenceParts.size());
        return Collections.unmodifiableList(mappedSentenceParts);
    }




}
