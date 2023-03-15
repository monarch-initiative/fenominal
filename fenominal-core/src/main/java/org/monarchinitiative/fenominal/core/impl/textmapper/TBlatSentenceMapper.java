package org.monarchinitiative.fenominal.core.impl.textmapper;

import org.monarchinitiative.fenominal.core.impl.corenlp.SimpleSentence;
import org.monarchinitiative.fenominal.core.impl.corenlp.SimpleToken;
import org.monarchinitiative.fenominal.core.impl.corenlp.StopWords;
import org.monarchinitiative.fenominal.core.impl.decorators.DecorationProcessorService;
import org.monarchinitiative.fenominal.core.impl.decorators.TokenDecoratorService;
import org.monarchinitiative.fenominal.core.impl.hpo.HpoMatcher;
import org.monarchinitiative.fenominal.core.impl.kmer.KmerDB;
import org.monarchinitiative.fenominal.core.impl.kmer.TBlatMatchingStrategy;
import org.monarchinitiative.fenominal.core.impl.kmer.TBlatUtil;
import org.monarchinitiative.fenominal.core.impl.lexical.LexicalResources;
import org.monarchinitiative.fenominal.model.impl.DetailedMinedTerm;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TBlatSentenceMapper extends AbstractSentenceMapper implements SentenceMapper {

    private final KmerDB kmerDB;

    private final LexicalResources lexicalResources;

    public TBlatSentenceMapper(KmerDB kmerDB, LexicalResources lexicalResources,
                               HpoMatcher hpoMatcher, TokenDecoratorService tokenDecoratorService,
                               DecorationProcessorService decorationProcessorService) {
        super(hpoMatcher, tokenDecoratorService, decorationProcessorService);
        this.kmerDB = kmerDB;
        this.lexicalResources = lexicalResources;
    }

    @Override
    public List<DetailedMinedTerm> mapSentence(SimpleSentence ss) {
        List<SimpleToken> nonStopWords = ss.getTokens().stream()
                .filter(Predicate.not(token -> StopWords.isStop(token.getToken())))
                .collect(Collectors.toList());
        nonStopWords = this.tokenDecoratorService.decorate(nonStopWords);

        Map<String, List<String>> tokenKmers = new LinkedHashMap<>();
        Map<String, List<String>> inverseKmerTokens = new LinkedHashMap<>();
        for (SimpleToken simpleToken : nonStopWords) {
            String token = simpleToken.getLowerCaseToken();
            if (token.length() < 5) {
                continue;
            }
            if (!tokenKmers.containsKey(token)) {
                if (!kmerDB.hasToken(token)) {
                    List<String> kmers = TBlatUtil.kmers(token, 3);
                    tokenKmers.put(token, kmers);
                    for (String kmer : kmers) {
                        List<String> tokenList = new ArrayList<>();
                        if (inverseKmerTokens.containsKey(kmer)) {
                            tokenList = inverseKmerTokens.get(kmer);
                        }
                        if (!tokenList.contains(token)) {
                            tokenList.add(token);
                        }
                        inverseKmerTokens.put(kmer, tokenList);
                    }

                }
            }
        }

        Map<String, String> matches = new LinkedHashMap<>();
        List<String> candidateTokens = kmerDB.computeCandidateTokensForKmerList(inverseKmerTokens);
        for (String typoToken : tokenKmers.keySet()) {
            double maxScore = 0.0;
            String optimalCandidate = null;
            for (String ontoCandidate : candidateTokens) {
                List<String> typoKmers = tokenKmers.get(typoToken);
                List<String> ontoKmers = kmerDB.getKmers(ontoCandidate);

                double score = new TBlatMatchingStrategy(ontoKmers, typoKmers, this.lexicalResources.getTrigramStates()).matchKmers();
                if (score > maxScore) {
                    maxScore = score;
                    optimalCandidate = ontoCandidate;
                }
            }

            if (optimalCandidate == null) {
                matches.put(typoToken, typoToken);
            } else {
                double optimalThreshold = this.lexicalResources.getThresholdForLength(optimalCandidate.length());
                if (maxScore >= optimalThreshold) {
                    matches.put(typoToken, optimalCandidate);
                } else {
                    matches.put(typoToken, typoToken);
                }
            }
        }

        List<SimpleToken> rebuiltTokenList = this.rebuildSimpleTokens(nonStopWords, matches);
        return super.mapSentence(rebuiltTokenList);
    }

    private List<SimpleToken> rebuildSimpleTokens(List<SimpleToken> nonStopWords, Map<String, String> matches) {
        List<SimpleToken> result = new ArrayList<>();
        for (SimpleToken simpleToken : nonStopWords) {
            String token = simpleToken.getLowerCaseToken();
            if (token.length() < 5) {
                result.add(simpleToken);
                continue;
            }
            if (kmerDB.hasToken(token)) {
                result.add(simpleToken);
            } else {
                result.add(new SimpleToken(matches.get(token), simpleToken.getToken(),
                        simpleToken.getStartpos(), simpleToken.getEndpos()));
            }
        }
        return result;
    }

}
