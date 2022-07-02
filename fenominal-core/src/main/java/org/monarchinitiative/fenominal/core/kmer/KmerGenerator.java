package org.monarchinitiative.fenominal.core.kmer;

import org.monarchinitiative.fenominal.core.corenlp.FmCoreDocument;
import org.monarchinitiative.fenominal.core.corenlp.SimpleToken;
import org.monarchinitiative.fenominal.core.corenlp.StopWords;
import org.monarchinitiative.fenominal.core.hpo.HpoLoader;
import org.monarchinitiative.fenominal.core.textmapper.TextMapperUtil;
import org.monarchinitiative.fenominal.json.JsonHpoParser;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class KmerGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(KmerGenerator.class);

    private Map<String, TermId> termMap;

    private KmerDB kmerDB;

    public KmerGenerator(KmerDB kmerDB) {
        this.kmerDB = kmerDB;
    }

    public KmerGenerator(Ontology ontology) {
        LOGGER.info("Loading ontology ...");
        HpoLoader hpoLoader = new HpoLoader(ontology);

        /**
         * TODO: textToTerm inside the HPOLoader does not distinguish between the various types of synonyms;
         * We probably should distinguish ...
         */
        this.termMap = hpoLoader.textToTermMap();
        this.kmerDB = new KmerDB();
    }

    public void doKMers(int k) {
        LOGGER.info("Generating k-mers: {}", k);
        KmerDBK kmerDBK = new KmerDBK();
        double sTime = System.currentTimeMillis();
        for (String termText : termMap.keySet()) {
            String hpoId = termMap.get(termText).getId();
            /**
             * Reusing the text processing component for consistency. What happens though with terms that contain punctuation?
             */
            FmCoreDocument fmCoreDocument = new FmCoreDocument(termText);

            /**
             * Expecting exactly one sentence from one term. Not sure what to do with it if it's more than one sentence ...
             */
            if (fmCoreDocument.getSentences().size() != 1) {
                LOGGER.warn("Unable to process term [{}]: {}", hpoId, termText);
                continue;
            }

            List<SimpleToken> tokens = fmCoreDocument.getSentences().get(0).getTokens()
                    .stream()
                    .filter(Predicate.not(token -> StopWords.isStop(token.getToken())))
                    .collect(Collectors.toList());
            List<String> flattenedLabel = tokens.stream().map(SimpleToken::getLowerCaseToken).collect(Collectors.toList());
            this.kmerDB.addLabel(termText, flattenedLabel);
            for (SimpleToken simpleToken : tokens) {
                List<String> kmers = TextMapperUtil.kmers(simpleToken.getLowerCaseToken(), k);

                for (String kmer : kmers) {
                    kmerDBK.add(kmer, hpoId, termText);
                }
            }
        }

        this.kmerDB.add(k, kmerDBK);
        double eTime = System.currentTimeMillis();
        LOGGER.info("Done generating k-mers [{}]: {}s", k, (eTime - sTime) / 1000);
    }

    public void serialize(String fileName) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(fileName);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(this.kmerDB);
        objectOutputStream.flush();
        objectOutputStream.close();
    }

    public void print() {
        LOGGER.info(this.kmerDB.toString());
    }

    public static KmerGenerator loadKmerDB(String fileName) throws IOException, ClassNotFoundException {
        FileInputStream fileInputStream = new FileInputStream(fileName);
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        KmerDB kmerDB = (KmerDB) objectInputStream.readObject();
        objectInputStream.close();
        return new KmerGenerator(kmerDB);
    }

    public static void main(String[] args) {
        Ontology hpo = JsonHpoParser.loadOntology("/home/tudor/dev/fenominal/fenominal-core/src/test/resources/hpo/hp.json");
        KmerGenerator kmerGenerator = new KmerGenerator(hpo);

        kmerGenerator.doKMers(3);
        kmerGenerator.doKMers(4);
        kmerGenerator.doKMers(5);
        try {
            kmerGenerator.serialize("/home/tudor/tmp/kmer.ser");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main2(String[] args) {
        try {
            KmerGenerator kmerGenerator = KmerGenerator.loadKmerDB("/home/tudor/tmp/test.ser");
            kmerGenerator.print();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
