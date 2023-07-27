package org.monarchinitiative.fenominal.core.impl.kmer;

import org.monarchinitiative.fenominal.core.impl.corenlp.FmCoreDocument;
import org.monarchinitiative.fenominal.core.impl.corenlp.SimpleToken;
import org.monarchinitiative.fenominal.core.impl.corenlp.StopWords;
import org.monarchinitiative.fenominal.core.impl.hpo.HpoLoader;
import org.monarchinitiative.phenol.ontology.data.MinimalOntology;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class KmerGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(KmerGenerator.class);

    private final static int KMER_SIZE = 3;

    private final Map<String, TermId> termMap;

    private final KmerDB kmerDB;

    public KmerGenerator(MinimalOntology ontology) {
        LOGGER.info("Loading ontology (term count: {})", ontology.getTerms().size());
        HpoLoader hpoLoader = new HpoLoader(ontology);
        this.termMap = hpoLoader.textToTermMap();
        this.kmerDB = new KmerDB(KMER_SIZE);

        this.doKMers();
    }

    public void doKMers() {
        LOGGER.info("Generating k-mers: {}", KMER_SIZE);
        double sTime = System.currentTimeMillis();
        for (String termText : termMap.keySet()) {
            String hpoId = termMap.get(termText).getId();
            //Reusing the text processing component for consistency. What happens though with terms that contain punctuation?
            FmCoreDocument fmCoreDocument = new FmCoreDocument(termText);
            // Expecting exactly one sentence from one term. Not sure what to do with it if it's more than one sentence ...
            if (fmCoreDocument.getSentences().size() != 1) {
                LOGGER.warn("Unable to process term [{}]: {}", hpoId, termText);
                continue;
            }
            List<SimpleToken> tokens = fmCoreDocument.getSentences().get(0).getTokens()
                    .stream()
                    .filter(Predicate.not(token -> StopWords.isStop(token.getToken()))).toList();
            List<String> flattenedLabel = tokens.stream().map(SimpleToken::getLowerCaseToken).collect(Collectors.toList());
            this.kmerDB.addLabel(hpoId, flattenedLabel);
        }

        double eTime = System.currentTimeMillis();
        LOGGER.info("Done generating k-mers [{}]: {}s", KMER_SIZE, (eTime - sTime) / 1000);
    }

    public void print() {
        LOGGER.info(this.kmerDB.toString());
    }

    public Optional<KmerDB> getKmerDB() {
        return Optional.ofNullable(kmerDB);
    }

}
