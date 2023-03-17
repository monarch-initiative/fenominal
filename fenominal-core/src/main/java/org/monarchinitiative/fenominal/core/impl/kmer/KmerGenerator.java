package org.monarchinitiative.fenominal.core.impl.kmer;

import org.monarchinitiative.fenominal.core.impl.corenlp.FmCoreDocument;
import org.monarchinitiative.fenominal.core.impl.corenlp.SimpleToken;
import org.monarchinitiative.fenominal.core.impl.corenlp.StopWords;
import org.monarchinitiative.fenominal.core.impl.hpo.HpoLoader;
//import org.monarchinitiative.fenominal.json.JsonHpoParser;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class KmerGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(KmerGenerator.class);

    private final static int KMER_SIZE = 3;

    private Map<String, TermId> termMap;

    private final KmerDB kmerDB;

    public KmerGenerator(KmerDB kmerDB) {
        this.kmerDB = kmerDB;
    }

    public KmerGenerator(Ontology ontology) {
        LOGGER.info("Loading ontology (term count: {})", ontology.countAllTerms());
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

    @Deprecated
    public void serialize(File fileName) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(fileName);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(this.kmerDB);
        objectOutputStream.flush();
        objectOutputStream.close();
    }

    public void print() {
        LOGGER.info(this.kmerDB.toString());
    }

    @Deprecated
    public static KmerGenerator loadKmerDB(String fileName) throws IOException, ClassNotFoundException {
        FileInputStream fileInputStream = new FileInputStream(fileName);
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        KmerDB kmerDB = (KmerDB) objectInputStream.readObject();
        objectInputStream.close();
        return new KmerGenerator(kmerDB);
    }

    public Optional<KmerDB> getKmerDB() {
        return Optional.ofNullable(kmerDB);
    }

}
