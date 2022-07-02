package org.monarchinitiative.fenominal.core.kmer;

import org.monarchinitiative.fenominal.core.corenlp.SimpleToken;
import org.monarchinitiative.fenominal.core.textmapper.TextMapperUtil;

import java.util.*;
import java.util.stream.Collectors;

public class KmerMatchingStrategy {

    private KmerDB kmerDB;
    private Map<Integer, Map<String, List<String>>> kmerData;
    private List<SimpleToken> tokens;

    private Map<String, Map<Integer, List<String>>> hpoDict;
    private Map<String, Map<List<SimpleToken>, Double>> candidates;

    public KmerMatchingStrategy(Map<Integer, Map<String, List<String>>> kmerData, List<SimpleToken> tokens, KmerDB kmerDB) {
        this.kmerData = kmerData;
        this.tokens = tokens;
        this.kmerDB = kmerDB;

        this.hpoDict = new LinkedHashMap<>();
        this.candidates = new LinkedHashMap<>();
    }

    public Map<String, Map<List<SimpleToken>, Double>> process() {
        for (int idx : this.kmerData.keySet()) {
            for (String hpoId : this.kmerData.get(idx).keySet()) {
                Map<Integer, List<String>> map = this.hpoDict.containsKey(hpoId) ? this.hpoDict.get(hpoId) : new HashMap<>();
                map.put(idx, this.kmerData.get(idx).get(hpoId));
                this.hpoDict.put(hpoId, map);
            }
        }

        for (String hpoId : this.hpoDict.keySet()) {
            List<Integer> sortedIdx = this.hpoDict.get(hpoId).keySet().stream().collect(Collectors.toList());
            Collections.sort(sortedIdx);

            Map<List<SimpleToken>, Double> matches = new LinkedHashMap();

            List<Integer> currentList = new ArrayList<>();
            int prevIdx = -1;

            for (int idx : sortedIdx) {
                if (prevIdx == -1) {
                    prevIdx = idx;
                    currentList.add(idx);
                    continue;
                }
                if (idx - prevIdx == 1) {
                    prevIdx = idx;
                    currentList.add(idx);
                } else {
                    List<String> labels = hpoDict.get(hpoId).get(idx);
                    for (String label : labels) {
                        int labelLength = this.kmerDB.getLabelLength(label);
                        List<String> labelTokens = this.kmerDB.getLabelTokens(label);
                        if (labelLength == -1 || labelTokens == null) {
                            //TODO: THIS SHOULD NEVER HAPPEN!!!
                            System.out.println("This should not happen !!!");
                            continue;
                        }

                        if (labelLength == currentList.size()) {
                            List<SimpleToken> toks = this.getTokens(currentList);
                            matches.put(toks, this.computeMatch(labelTokens, toks.stream().map(SimpleToken::getLowerCaseToken).collect(Collectors.toList())));
                        }
                    }


                    currentList = new ArrayList<>();
                    prevIdx = idx;
                    currentList.add(idx);
                }
            }

            if (!currentList.isEmpty()) {
                for (int idx : sortedIdx) {
                    List<String> labels = hpoDict.get(hpoId).get(idx);
                    for (String label : labels) {
                        int labelLength = this.kmerDB.getLabelLength(label);
                        List<String> labelTokens = this.kmerDB.getLabelTokens(label);
                        if (labelLength == -1 || labelTokens == null) {
                            //TODO: THIS SHOULD NEVER HAPPEN!!!
                            System.out.println("This should not happen !!!");
                            continue;
                        }

                        if (labelLength == currentList.size()) {
                            List<SimpleToken> toks = this.getTokens(currentList);
                            matches.put(toks, this.computeMatch(labelTokens, toks.stream().map(SimpleToken::getLowerCaseToken).collect(Collectors.toList())));
                        }
                    }
                }
            }
            if (!matches.isEmpty()) {
                this.candidates.put(hpoId, matches);
            }
        }

        return this.candidates;
    }

    private List<SimpleToken> getTokens(List<Integer> idxList) {
        List<SimpleToken> toks = new ArrayList<>();
        for (int idx : idxList) {
            toks.add(this.tokens.get(idx));
        }
        return toks;
    }

    private double computeMatch(List<String> labelTokens, List<String> toks) {
        List<String> done = new ArrayList<>();
        double totalSim = 0;
        for (String token : labelTokens) {
            double max = 0;
            String current = null;
            for (String tok : toks) {
                double sim = TextMapperUtil.setBigramRatio(token, tok);
                if (sim > max) {
                    if (current != null) {
                        if (!done.contains(current)) {
                            max = sim;
                            current = tok;
                        }
                    } else {
                        max = sim;
                        current = tok;
                    }
                }
            }

            done.add(current);
            totalSim += max;
        }

        return totalSim / labelTokens.size();

    }
}