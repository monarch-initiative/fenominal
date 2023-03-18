package org.monarchinitiative.fenominal.core.impl.kmer;

import org.jgrapht.alg.util.Pair;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TBlatMatchingStrategy {

    private final Map<String, Map<String, Double>> trigramStates;

    private final List<String> ontoTokenKmers;

    private final List<String> targetKmers;

    public TBlatMatchingStrategy(List<String> ontoTokenKmers, List<String> targetKmers,
                                 Map<String, Map<String, Double>> trigramStates) {
        this.ontoTokenKmers = ontoTokenKmers;
        this.targetKmers = targetKmers;
        this.trigramStates = trigramStates;
    }

    private Pair<Map<Integer, String>, Map<String, List<String>>> prepareTokenSet(List<String> tokenSet) {
        Map<Integer, String> originalTokenSet = new LinkedHashMap<>();
        Map<String, List<String>> originalTokenIdxs = new LinkedHashMap<>();
        int idx = 0;
        for (String kmer : tokenSet) {
            originalTokenSet.put(idx, kmer);
            List<String> idxList = new ArrayList<>();
            if (originalTokenIdxs.containsKey(kmer)) {
                idxList = originalTokenIdxs.get(kmer);
            }
            idxList.add(Integer.toString(idx));
            originalTokenIdxs.put(kmer, idxList);
            idx += 1;
        }
        return Pair.of(originalTokenSet, originalTokenIdxs);
    }

    private double scoreForSet(List<String> kmerSet) {
        double sum = 0;
        for (String el : kmerSet) {
            double value = 1.0;
            if (this.trigramStates.containsKey(el)) {
                Map<String, Double> kmerData = this.trigramStates.get(el);
                if (kmerData.containsKey(el)) {
                    value = kmerData.get(el);
                } else {
                    value = TBlatUtil.avg_of_top_k(kmerData, 3);
                }
            }
            sum += value;
        }
        return sum;
    }

    public double matchKmers() {
        double score = 0.0;

        Pair<Map<Integer, String>, Map<String, List<String>>> res = this.prepareTokenSet(this.targetKmers);
        Map<Integer, String> originalTokenSet = res.getFirst();
        Map<String, List<String>> originalTokenIdxs = res.getSecond();

        res = this.prepareTokenSet(this.ontoTokenKmers);
        Map<Integer, String> kmerTokenSet = res.getFirst();
        Map<String, List<String>> kmerTokenIdxs = res.getSecond();

        double sum = this.scoreForSet(this.targetKmers);
        /*
         * Step 1: Find all and remove exact matches
         */
        int exactMatches = 0;
        List<String> to_remove = new ArrayList<>();

        for (String kmer : kmerTokenIdxs.keySet()) {
            List<String> kmerIdxs = kmerTokenIdxs.get(kmer);
            double kmerSelfVal = 0.01;
            if (this.trigramStates.containsKey(kmer)) {
                if (this.trigramStates.get(kmer).containsKey(kmer)) {
                    kmerSelfVal = this.trigramStates.get(kmer).get(kmer);
                } else {
                    kmerSelfVal = TBlatUtil.avg_of_top_k(this.trigramStates.get(kmer), 3);
                }
            }

            if (originalTokenIdxs.containsKey(kmer)) {
                List<String> tokenIdxs = originalTokenIdxs.get(kmer);
                List<String> intersect = kmerIdxs.stream()
                        .filter(tokenIdxs::contains)
                        .distinct()
                        .toList();

                if (!intersect.isEmpty()) {
                    exactMatches += intersect.size();
                    score += intersect.size() * kmerSelfVal;

                    for (String idx : intersect) {
                        kmerIdxs.remove(idx);
                        tokenIdxs.remove(idx);
                    }
                }

                if (tokenIdxs.size() == kmerIdxs.size()) {
                    exactMatches += tokenIdxs.size();
                    score += tokenIdxs.size() * kmerSelfVal;
                    originalTokenIdxs.remove(kmer);
                    to_remove.add(kmer);
                } else {
                    if (tokenIdxs.size() < kmerIdxs.size()) {
                        exactMatches += tokenIdxs.size();
                        score += tokenIdxs.size() * kmerSelfVal;
                        originalTokenIdxs.remove(kmer);
                        for (int i = 0; i < tokenIdxs.size(); i++) {
                            kmerIdxs.remove(i);
                        }
                    } else {
                        exactMatches += kmerIdxs.size();
                        score += kmerIdxs.size() * kmerSelfVal;
                        to_remove.add(kmer);
                        for (int i = 0; i < kmerIdxs.size(); i++) {
                            tokenIdxs.remove(i);
                        }
                        originalTokenIdxs.put(kmer, tokenIdxs);
                    }
                }
            }
        }
        for (String kmer : to_remove) {
            kmerTokenIdxs.remove(kmer);
        }

        List<Integer> to_remove_idx = new ArrayList<>();
        for (int idx : originalTokenSet.keySet()) {
            String kmer = originalTokenSet.get(idx);
            if (!originalTokenIdxs.containsKey(kmer)) {
                to_remove_idx.add(idx);
            } else {
                if (!originalTokenIdxs.get(kmer).contains(idx)) {
                    to_remove_idx.add(idx);
                }
            }
        }
        for (int idx : to_remove_idx) {
            originalTokenSet.remove(idx);
        }

        to_remove_idx = new ArrayList<>();
        for (int idx : kmerTokenSet.keySet()) {
            String kmer = kmerTokenSet.get(idx);
            if (!kmerTokenIdxs.containsKey(kmer)) {
                to_remove_idx.add(idx);
            } else {
                if (!kmerTokenIdxs.get(kmer).contains(idx)) {
                    to_remove_idx.add(idx);
                }
            }
        }

        for (int idx : to_remove_idx) {
            kmerTokenSet.remove(idx);
        }

        List<Integer> matches = new ArrayList<>();
        for (int idx : originalTokenSet.keySet()) {
            String kmer = originalTokenSet.get(idx);

            to_remove_idx = new ArrayList<>();
            for (int idx2 : kmerTokenSet.keySet()) {
                if (to_remove_idx.contains(idx2)) {
                    continue;
                }
                if (kmerTokenSet.containsKey(idx2 + 1)) {
//                    merge = kmerTokenSet[idx2] + kmerTokenSet[idx2 + 1]
                    String merge = kmerTokenSet.get(idx2) + kmerTokenSet.get(idx2 + 1);
//                    if (kmer[:2] ==merge[:2]and kmer[ -2:] ==merge[-2:]){
                    if (kmer.substring(0, 2).equalsIgnoreCase(merge.substring(0, 2)) && kmer.substring(kmer.length() - 2).equalsIgnoreCase(merge.substring(merge.length() - 2))) {
                        matches.add(idx);
                        to_remove_idx.add(idx2);
                        to_remove_idx.add(idx2 + 1);
                    }
                }
            }

            for (int idx2 : to_remove_idx) {
                List<String> tokenIdxs = kmerTokenIdxs.get(kmerTokenSet.get(idx2));
                tokenIdxs.remove(Integer.toString(idx2));
                if (tokenIdxs.isEmpty()) {
                    kmerTokenIdxs.remove(kmerTokenSet.get(idx2));
                    kmerTokenSet.remove(idx2);
                }
            }
        }

        for (int idx : matches) {
            String kmer = originalTokenSet.get(idx);
            double kmerSelfVal = 0.01;
            if (this.trigramStates.containsKey(kmer)) {
                if (this.trigramStates.get(kmer).containsKey(kmer)) {
                    kmerSelfVal = this.trigramStates.get(kmer).get(kmer);
                } else {
                    kmerSelfVal = TBlatUtil.avg_of_top_k(this.trigramStates.get(kmer), 3);
                }
            }
            exactMatches += 1;
            score += kmerSelfVal;
            List<String> tokenIdxs = originalTokenIdxs.get(originalTokenSet.get(idx));
            tokenIdxs.remove(tokenIdxs.indexOf(Integer.toString(idx)));
            if (tokenIdxs.isEmpty()) {
                originalTokenIdxs.remove(originalTokenSet.get(idx));
                originalTokenSet.remove(idx);
            }
        }

        /*
         * Step 2: Match remaining kmers to left-overs from original string
         */
        Map<String, Integer> kmer_leftovers = new LinkedHashMap<>();
        if (originalTokenIdxs.isEmpty()) {
            for (String kmer : kmerTokenIdxs.keySet()) {
                kmer_leftovers.put(kmer, kmerTokenIdxs.get(kmer).size());
            }
        }

        while (kmerTokenIdxs.size() > 0) {
            if (originalTokenIdxs.isEmpty()) {
                break;
            }

            to_remove = new ArrayList<>();

            for (String kmer : kmerTokenIdxs.keySet()) {
                if (originalTokenIdxs.isEmpty()) {
                    kmer_leftovers.put(kmer, kmerTokenIdxs.get(kmer).size());
                    continue;
                }

                List<String> kmerIdxs = kmerTokenIdxs.get(kmer);

                if (this.trigramStates.containsKey(kmer)) {
                    /*
                     * There are state transitions
                     */
                    String found = null;
                    Map<String, Double> kmerData = this.trigramStates.get(kmer);
                    double currentScore = -1;

                    for (String el : originalTokenIdxs.keySet()) {
                        if (kmerData.containsKey(el)) {
                            if (kmerData.get(el) > currentScore) {
                                currentScore = kmerData.get(el);
                                found = el;
                            }
                        }
                    }

                    if (found != null) {
                        /*
                         * An appropriate state transition was found
                         */
                        List<String> tokenIdxs = originalTokenIdxs.get(found);
                        List<String> intersect = kmerIdxs.stream()
                                .filter(tokenIdxs::contains)
                                .distinct()
                                .toList();

                        if (!intersect.isEmpty()) {
                            score += currentScore * intersect.size();
                            for (String idx : intersect) {
                                kmerIdxs.remove(idx);
                                tokenIdxs.remove(idx);
                            }
                        } else {
                            score += currentScore;
                            kmerIdxs.remove(0);
                            tokenIdxs.remove(0);
                        }

                        if (tokenIdxs.isEmpty()) {
                            originalTokenIdxs.remove(found);
                        }
                        if (kmerIdxs.isEmpty()) {
                            to_remove.add(kmer);
                        }
                    } else {
                        /*
                         * No appropriate state transition was found
                         * Find best matching pair based on transition type
                         * Assign value of a self-transition * probability of transition type
                         */

                        for (String el : originalTokenIdxs.keySet()) {
                            int transition_type = TBlatUtil.detect_type_of_transition(el, kmer);
                            if (transition_type != TBlatUtil.NO_MATCH) {
                                double value = 0.01;
                                if (this.trigramStates.containsKey(el)) {
                                    kmerData = this.trigramStates.get(el);
                                    if (kmerData.containsKey(el)) {
                                        value = kmerData.get(el);
                                    } else {
                                        value = TBlatUtil.avg_of_top_k(kmerData, 3);
                                    }
                                }
                                double scoreVal = value * TBlatUtil.TRANSITION_PROBABILITY.get(transition_type);
                                if (scoreVal > currentScore) {
                                    currentScore = scoreVal;
                                    found = el;
                                }
                            }

                        }

                        if (found == null) {
                            to_remove.add(kmer);
                            kmer_leftovers.put(kmer, kmerTokenIdxs.get(kmer).size());
                        } else {
                            List<String> tokenIdxs = originalTokenIdxs.get(found);
                            List<String> intersect = kmerIdxs.stream()
                                    .filter(tokenIdxs::contains)
                                    .distinct()
                                    .toList();
                            if (!intersect.isEmpty()) {
                                score += currentScore * intersect.size();
                                for (String idx : intersect) {
                                    kmerIdxs.remove(idx);
                                    tokenIdxs.remove(idx);
                                }
                            } else {
                                score += currentScore;
                                kmerIdxs.remove(0);
                                tokenIdxs.remove(0);
                            }

                            if (tokenIdxs.isEmpty()) {
                                originalTokenIdxs.remove(found);
                            }
                            if (kmerIdxs.isEmpty()) {
                                to_remove.add(kmer);
                            }
                        }
                    }

                } else {

                    /*
                     * There are NO state transitions
                     * Find best matching pair based on transition type
                     * Assign value of a self-transition * probability of transition type
                     */

                    String found = null;
                    double currentScore = -1;

                    for (String el : originalTokenIdxs.keySet()) {
                        int transition_type = TBlatUtil.detect_type_of_transition(el, kmer);
                        if (transition_type != TBlatUtil.NO_MATCH) {
                            double value = 0.01;
                            if (this.trigramStates.containsKey(el)) {
                                Map<String, Double> kmerData = this.trigramStates.get(el);
                                if (kmerData.containsKey(el)) {
                                    value = kmerData.get(el);
                                } else {
                                    value = TBlatUtil.avg_of_top_k(kmerData, 3);
                                }
                            }
                            double scoreVal = value * TBlatUtil.TRANSITION_PROBABILITY.get(transition_type);
                            if (scoreVal > currentScore) {
                                currentScore = scoreVal;
                                found = el;
                            }
                        }
                    }

                    if (found == null) {
                        kmer_leftovers.put(kmer, kmerTokenIdxs.get(kmer).size());
                        to_remove.add(kmer);
                    } else {
                        List<String> tokenIdxs = originalTokenIdxs.get(found);
                        List<String> intersect = kmerIdxs.stream()
                                .filter(tokenIdxs::contains)
                                .distinct()
                                .toList();

                        if (!intersect.isEmpty()) {
                            score += currentScore * intersect.size();
                            for (String idx : intersect) {
                                kmerIdxs.remove(idx);
                                tokenIdxs.remove(idx);
                            }

                        } else {
                            score += currentScore;
                            kmerIdxs.remove(0);
                            tokenIdxs.remove(0);
                        }

                        if (tokenIdxs.isEmpty()) {
                            originalTokenIdxs.remove(found);
                        }
                        if (kmerIdxs.isEmpty()) {
                            to_remove.add(kmer);
                        }
                    }
                }

            }

            for (String kmer : to_remove) {
                kmerTokenIdxs.remove(kmer);
            }
        }

        /*
         * Check if there are left-over terms in originalTokenIdxs
         * - Penalty: the value of a self-transition for each kmer + (?) 1 / len(originalTokenIdxs) * value of self-transition
         */

        for (String el : originalTokenIdxs.keySet()) {
            double value = 0.01;
            if (this.trigramStates.containsKey(el)) {
                Map<String, Double> kmerData = this.trigramStates.get(el);
                if (kmerData.containsKey(el)) {
                    value = kmerData.get(el);
                } else {
                    value = TBlatUtil.avg_of_top_k(kmerData, 3);
                }
            }
            score = score - originalTokenIdxs.get(el).size() * value;
        }

        /*
         * Check if there are left-over terms in kmerTokenIdxs
         * - Penalty: the value of a self-transition for each kmer + (?) 1 / len(originalTokenIdxs) * value of self-transition
         */

        double penScore = score;
        if (!this.targetKmers.isEmpty()) {
            for (String el : kmer_leftovers.keySet()) {
                penScore = penScore - kmer_leftovers.get(el) * (sum / this.targetKmers.size());
            }
        }

        double min;
        if (this.targetKmers.size() % 2 == 0) {
            min = this.targetKmers.size() / 2;
        } else {
            min = (this.targetKmers.size() - 1) / 2;
        }
        if (exactMatches < min) {
            return 0.0;
        }

        return penScore;
    }
}
