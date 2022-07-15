package org.monarchinitiative.fenominal.core.impl.kmer;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

public class KmerDBK implements Serializable {

    @Serial
    private static final long serialVersionUID = 2L;

    private Map<String, Map<String, List<String>>> kmerSet;

    public KmerDBK() {
        kmerSet = new TreeMap<>();
    }

    public void add(String kmer, String hpoId, String labelAsString) {
        Map<String, List<String>> map = this.kmerSet.containsKey(kmer) ? this.kmerSet.get(kmer) : new HashMap<>();
        List<String> list = map.containsKey(hpoId) ? map.get(hpoId) : new ArrayList<>();
        if (!list.contains(labelAsString)) {
            list.add(labelAsString);
        }
        map.put(hpoId, list);
        this.kmerSet.put(kmer, map);
    }

    public Map<String, Map<String, List<String>>> getKmerSet() {
        return kmerSet;
    }

    public void setKmerSet(Map<String, Map<String, List<String>>> kmerSet) {
        this.kmerSet = kmerSet;
    }
}
