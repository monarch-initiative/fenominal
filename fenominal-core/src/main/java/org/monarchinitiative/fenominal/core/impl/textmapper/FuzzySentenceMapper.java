package org.monarchinitiative.fenominal.core.impl.textmapper;

import org.monarchinitiative.fenominal.model.impl.DetailedMinedTerm;
import org.monarchinitiative.fenominal.core.impl.corenlp.SimpleSentence;
import org.monarchinitiative.fenominal.core.impl.corenlp.SimpleToken;
import org.monarchinitiative.fenominal.core.impl.corenlp.StopWords;
import org.monarchinitiative.fenominal.core.impl.decorators.DecorationProcessorService;
import org.monarchinitiative.fenominal.core.impl.decorators.TokenDecoratorService;
import org.monarchinitiative.fenominal.core.impl.kmer.KmerDB;
import org.monarchinitiative.fenominal.core.impl.kmer.KmerMatchingStrategy;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class FuzzySentenceMapper implements SentenceMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(FuzzySentenceMapper.class);

    private final boolean valid;

    private final KmerDB kmerDB;

    private final int kmerSize;

    private final TokenDecoratorService tokenDecoratorService;

    private final DecorationProcessorService decorationProcessorService;

    public FuzzySentenceMapper(KmerDB kmerDB,
                               int kmerSize,
                               TokenDecoratorService tokenDecoratorService,
                               DecorationProcessorService decorationProcessorService) {
        this.valid = false;
        this.kmerSize = kmerSize;
        this.tokenDecoratorService = tokenDecoratorService;
        this.decorationProcessorService = decorationProcessorService;
        this.kmerDB = kmerDB;
    }



    public boolean isValid() {
        return valid;
    }

    @Override
    public List<DetailedMinedTerm> mapSentence(SimpleSentence ss) {
        List<SimpleToken> nonStopWords = ss.getTokens().stream()
                .filter(Predicate.not(token -> StopWords.isStop(token.getToken())))
                .collect(Collectors.toList());
        nonStopWords = this.tokenDecoratorService.decorate(nonStopWords);

        Map<Integer, Map<String, List<String>>> kmerData = new LinkedHashMap<>();
        for (int i = 0; i < nonStopWords.size(); i++) {
            List<String> kmers = TextMapperUtil.kmers(nonStopWords.get(i).getLowerCaseToken(), kmerSize);

            Map<String, Map<String, Integer>> tokenLevel = new LinkedHashMap<>();

            // For each k-mer retrieve HPO terms; keep count of HPO terms encountered in the context of this token
            for (String kmer : kmers) {
                Map<String, List<String>> hpoIds = kmerDB.getHPOIds(kmer, kmerSize);
                if (hpoIds != null) {
                    for (String hpoId : hpoIds.keySet()) {
                        Map<String, Integer> map = tokenLevel.containsKey(hpoId) ? tokenLevel.get(hpoId) : new HashMap<>();
                        for (String label : hpoIds.get(hpoId)) {
                            int count = map.getOrDefault(label, 0);
                            count++;
                            map.put(label, count);
                        }
                        tokenLevel.put(hpoId, map);
                    }
                }
            }

            int maxKmer = nonStopWords.get(i).getLowerCaseToken().length() - kmerSize + 1;
            double threshold = 1;
            if (maxKmer > 2) {
                threshold = (double) maxKmer / 2;
            }

            // Filter HPO terms to have in common at least half the max number of k-mers possible for the current token
            // Maintain a map from token index in the sentence to HPO terms
            Map<String, List<String>> finalList = new HashMap<>();
            for (String hpoId : tokenLevel.keySet()) {
                List<String> labelList = new ArrayList<>();
                for (String label : tokenLevel.get(hpoId).keySet()) {
                    if (tokenLevel.get(hpoId).get(label) >= threshold) {
                        labelList.add(label);
                    }
                }
                if (!labelList.isEmpty()) {
                    finalList.put(hpoId, labelList);
                }
            }
            kmerData.put(i, finalList);
        }

        KmerMatchingStrategy kmerMatchingStrategy = new KmerMatchingStrategy(kmerData, nonStopWords, this.kmerDB);
        Map<String, Map<List<SimpleToken>, Double>> matchedTerms = kmerMatchingStrategy.process();

        return this.orderResults(matchedTerms, nonStopWords);
    }

    private List<DetailedMinedTerm> orderResults(Map<String, Map<List<SimpleToken>, Double>> matchedTerms, List<SimpleToken> nonStopWords) {
        Map<Integer, List<DetailedMinedTerm>> candidates = new HashMap<>();
        for (String hpoId : matchedTerms.keySet()) {
            for (List<SimpleToken> chunk : matchedTerms.get(hpoId).keySet()) {
                double similarity = matchedTerms.get(hpoId).get(chunk);

                DetailedMinedTerm mappedSentencePart =
                        decorationProcessorService.process(chunk, nonStopWords, TermId.of("HP", hpoId), similarity);
                candidates.putIfAbsent(mappedSentencePart.getBegin(), new ArrayList<>());
                candidates.get(mappedSentencePart.getBegin()).add(mappedSentencePart);
            }
        }


        // FROM EXACT SENTENCE MATCHING
        // When we get here, we have zero, one, or more MappedSentenceParts.
        // Our heuristic is to take the longest match first
        // First get and sort the start positions
        List<Integer> startPosList = new ArrayList<>(candidates.keySet());
        Collections.sort(startPosList);
        int currentSpan = -1;
        List<DetailedMinedTerm> mappedSentencePartList = new ArrayList<>();
        for (int i : startPosList) {
            if (i < currentSpan) {
                continue;
            }
            List<DetailedMinedTerm> candidatesAtPositionI = candidates.get(i);
            DetailedMinedTerm longest = TextMapperUtil.getLongestPart(candidatesAtPositionI);
            mappedSentencePartList.add(longest);
            // advance to the last position of the current match
            // note that this is String position convention, and so the next hist could start at
            // currentSpan, but cannot be less than currentSpan without overlapping.
            currentSpan = longest.getEnd();
        }
        return mappedSentencePartList;
    }
}
