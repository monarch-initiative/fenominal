package org.monarchinitiative.fenominal.core.textmapper;


import org.monarchinitiative.fenominal.model.MappedSentencePart;
import org.monarchinitiative.fenominal.core.corenlp.SimpleSentence;
import org.monarchinitiative.fenominal.core.corenlp.SimpleToken;
import org.monarchinitiative.fenominal.core.corenlp.StopWords;
import org.monarchinitiative.fenominal.core.decorators.DecorationProcessorService;
import org.monarchinitiative.fenominal.core.decorators.TokenDecoratorService;
import org.monarchinitiative.fenominal.core.hpo.DefaultHpoMatcher;
import org.monarchinitiative.fenominal.core.hpo.HpoConceptHit;
import org.monarchinitiative.fenominal.core.lexical.LexicalResources;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * This is the default text miner for HPO. Here we follow these steps.
 * 1. Remove stop words
 * 2. Divide the sentence up into partitions with chunks of a defined length, where the
 * chunks go for i=1..10
 * 3. Use the {@link DefaultHpoMatcher} to match each chunk to ontology terms of the appropriate size
 * 4. Put the candidate into a map indexed by the start position of the match
 * 5. Better heuristic match -- search for the longest matches first. For matches of equal length,
 * use a heuristic to decide which to take
 * @author Peter Robinson
 */
public class OptimalSentenceMapper implements SentenceMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleSentenceMapper.class);
    private final DefaultHpoMatcher hpoMatcher;
    private final TokenDecoratorService tokenDecoratorService;
    private final DecorationProcessorService decorationProcessorService;


    public OptimalSentenceMapper(DefaultHpoMatcher hpoMatcher, LexicalResources lexicalResources){
        this.hpoMatcher = hpoMatcher;
        this.tokenDecoratorService = new TokenDecoratorService(lexicalResources);
        this.decorationProcessorService = new DecorationProcessorService();
    }

    public List<MappedSentencePart> mapSentence(SimpleSentence ss) {
        List<SimpleToken> nonStopWords = ss.getTokens().stream()
                .filter(Predicate.not(token -> StopWords.isStop(token.getToken())))
                .collect(Collectors.toList());
        nonStopWords = this.tokenDecoratorService.decorate(nonStopWords);
        // key -- sentence start position, value -- candidate matches.
        Map<Integer, List<MappedSentencePart>> candidates = new HashMap<>();
        int LEN = Math.min(10, nonStopWords.size()); // check for maximum of 10 words TODO -- what is best option here?
        for (int i = 1; i <= LEN; i++) {
            Partition<SimpleToken> partition = new Partition<>(nonStopWords, i);
            for (List<SimpleToken> chunk : partition) {
                if (chunk.size() < i) {
                    continue; // last portion is smaller, we will get it in corresponding loop
                }
                List<String> stringchunk = chunk.stream().map(SimpleToken::getToken).collect(Collectors.toList());
                Optional<HpoConceptHit> opt = this.hpoMatcher.getMatch(stringchunk);
                if (opt.isPresent()) {
                    TermId hpoId = opt.get().hpoConcept().getHpoId();
                    double TODO_DEFAULT_SIM = 1.0;
                    MappedSentencePart mappedSentencePart =
                            decorationProcessorService.process(chunk, nonStopWords, hpoId, TODO_DEFAULT_SIM);
//                            new MappedSentencePart(chunk, opt.get().getHpoId());
                    candidates.putIfAbsent(mappedSentencePart.getBegin(), new ArrayList<>());
                    candidates.get(mappedSentencePart.getBegin()).add(mappedSentencePart);
                }
            }
        }
        // When we get here, we have zero, one, or more MappedSentenceParts.
        // Our heuristic is to take the longest match first
        // First get and sort the start positions
        List<Integer> startPosList = new ArrayList<>(candidates.keySet());
        Collections.sort(startPosList);
        int currentSpan = -1;
        List<MappedSentencePart> mappedSentencePartList = new ArrayList<>();
        for (int i : startPosList) {
            if (i < currentSpan) {
                continue;
            }
            List<MappedSentencePart> candidatesAtPositionI = candidates.get(i);
            MappedSentencePart longest = getLongestPart(candidatesAtPositionI);
            mappedSentencePartList.add(longest);
            // advance to the last position of the current match
            // note that this is String position convention, and so the next hist could start at
            // currentSpan, but cannot be less than currentSpan without overlapping.
            currentSpan = longest.getEnd();
        }
        return mappedSentencePartList;
    }




    private List<MappedSentencePart> getBestCandidates(List<SimpleToken> nonStopWords ,
                                                       Map<Integer, List<MappedSentencePart>> candidates) {
        // arrange hits according to number of matches tokens
        Map<Integer, List<MappedSentencePart>> wordCountToSentencePartListMap = new HashMap<>();
        for (List<MappedSentencePart> sentencePartList : candidates.values()) {
            for (MappedSentencePart msp : sentencePartList) {
                int m = msp.getTokenCount();
                wordCountToSentencePartListMap.putIfAbsent(m, new ArrayList<>());
                wordCountToSentencePartListMap.get(m).add(msp);
            }
        }
        return List.of(); // TODO
    }

    private MappedSentencePart getLongestPart(List<MappedSentencePart> candidatesAtPositionI) {
        // we should be guaranteed to have at least one list entry -- TODO do we need to check?
        MappedSentencePart max = candidatesAtPositionI.get(0);
        for (int i = 1; i < candidatesAtPositionI.size(); i++) {
            if (candidatesAtPositionI.get(i).getEnd() > max.getEnd()) {
                max = candidatesAtPositionI.get(i);
            }
        }
        return max;
    }
}
