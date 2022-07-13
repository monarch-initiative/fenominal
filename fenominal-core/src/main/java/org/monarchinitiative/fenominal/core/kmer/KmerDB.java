package org.monarchinitiative.fenominal.core.kmer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

public class KmerDB implements Serializable {
    private static Logger LOGGER = LoggerFactory.getLogger(KmerDB.class);
    @Serial
    private static final long serialVersionUID = 2L;

    private Map<Integer, KmerDBK> kmerDBKMap;

    private Map<String, List<String>> labelTokens;

    public KmerDB() {
        kmerDBKMap = new TreeMap<>();
        labelTokens = new HashMap<>();
    }

    public void add(int k, KmerDBK kmerDBK) {
        this.kmerDBKMap.put(k, kmerDBK);
    }

    public void addLabel(String labelAsString, List<String> tokenList) {
        if (!labelTokens.containsKey(labelAsString)) {
            labelTokens.put(labelAsString, tokenList);
        }
    }

    public Map<String, List<String>> getHPOIds(String kmer, int kmerSize) {
        if (this.kmerDBKMap.containsKey(kmerSize)) {
            return this.kmerDBKMap.get(kmerSize).getKmerSet().get(kmer);
        }
        return Map.of();
    }

    public Map<Integer, KmerDBK> getKmerDBKMap() {
        return kmerDBKMap;
    }

    public void setKmerDBKMap(Map<Integer, KmerDBK> kmerDBKMap) {
        this.kmerDBKMap = kmerDBKMap;
    }

    public int getLabelLength(String label) {
        if (labelTokens.containsKey(label)) {
            return labelTokens.get(label).size();
        }
        return -1;
    }

    public List<String> getLabelTokens(String label) {
        if (labelTokens.containsKey(label)) {
            return labelTokens.get(label);
        }

        return null;
    }

    public static Optional<KmerDB> loadKmerDB(String file) {
        LOGGER.info("Loading K-mer DB from: {}", file);
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            KmerDB kmerDB = (KmerDB) objectInputStream.readObject();
            objectInputStream.close();
            return Optional.of(kmerDB);
        } catch (IOException | ClassNotFoundException e) {
            LOGGER.error("Unable to load K-mer DB file [{}]: {}", file, e.getMessage(), e);
            return Optional.empty();
        }
    }
}
