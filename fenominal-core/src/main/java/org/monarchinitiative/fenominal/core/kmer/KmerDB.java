package org.monarchinitiative.fenominal.core.kmer;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class KmerDB implements Serializable {

    @Serial
    private static final long serialVersionUID = -7533091038176758334L;

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
        return null;
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
}
