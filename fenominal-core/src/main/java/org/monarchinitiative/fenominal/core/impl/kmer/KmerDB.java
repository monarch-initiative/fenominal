package org.monarchinitiative.fenominal.core.impl.kmer;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class KmerDB implements Serializable {

    @Serial
    private static final long serialVersionUID = 2L;

    private final int kmerSize;

    private final Map<String, List<List<String>>> hpoLabels;

    private final Map<String, List<String>> tokensToHpo;

    private final Map<String, List<String>> tokenToKmers;

    private final Map<String, List<String>> kmerToTokens;

    public KmerDB(int kmerSize) {
        this.kmerSize = kmerSize;
        hpoLabels = new LinkedHashMap<>();
        tokensToHpo = new LinkedHashMap<>();
        tokenToKmers = new LinkedHashMap<>();
        kmerToTokens = new LinkedHashMap<>();
    }

    public void addLabel(String hpoId, List<String> tokenList) {
        List<List<String>> lists = new ArrayList<>();
        if (hpoLabels.containsKey(hpoId)) {
            lists = hpoLabels.get(hpoId);
        }
        lists.add(tokenList);
        hpoLabels.put(hpoId, lists);


        for (String token : tokenList) {
            List<String> hpoList = new ArrayList<>();
            if (tokensToHpo.containsKey(token)) {
                hpoList = tokensToHpo.get(token);
            }
            if (!hpoList.contains(hpoId)) {
                hpoList.add(hpoId);
            }
            tokensToHpo.put(token, hpoList);

            if (token.length() < 5) {
                continue;
            }
            List<String> kmers = TBlatUtil.kmers(token, kmerSize);
            tokenToKmers.put(token, kmers);
            for (String kmer : kmers) {
                List<String> tokens = new ArrayList<>();
                if (kmerToTokens.containsKey(kmer)) {
                    tokens = kmerToTokens.get(kmer);
                }
                if (!tokens.contains(token)) {
                    tokens.add(token);
                }
                kmerToTokens.put(kmer, tokens);
            }
        }

    }

    public boolean hasToken(String token) {
        return this.tokensToHpo.containsKey(token);
    }

    public List<String> getKmers(String token) {
        return this.tokenToKmers.get(token);
    }

    @Deprecated
    public Map<String, List<String>> getHPOIds(String kmer) {
        Map<String, List<String>> result = new LinkedHashMap<>();
        return Map.of();
    }

    /**
     * This is implemented in the worst possible way - highly inefficient. We need to think of a better way to do it.
     */
    public List<String> computeCandidateTokensForKmerList(Map<String, List<String>> kmerList) {
        List<String> result = new ArrayList<>();

        for (String token : tokenToKmers.keySet()) {
            for (String kmer : tokenToKmers.get(token)) {
                if (kmerList.containsKey(kmer)) {
                    result.add(token);
                    break;
                }
            }
        }

        return result;
    }
}
