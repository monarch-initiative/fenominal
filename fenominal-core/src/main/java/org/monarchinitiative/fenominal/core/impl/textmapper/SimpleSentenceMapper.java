package org.monarchinitiative.fenominal.core.impl.textmapper;

import org.monarchinitiative.fenominal.core.impl.lexical.LexicalResources;
import org.monarchinitiative.fenominal.model.impl.DetailedMinedTerm;
import org.monarchinitiative.fenominal.core.impl.corenlp.SimpleSentence;
import org.monarchinitiative.fenominal.core.impl.corenlp.SimpleToken;
import org.monarchinitiative.fenominal.core.impl.corenlp.StopWords;
import org.monarchinitiative.fenominal.core.impl.decorators.DecorationProcessorService;
import org.monarchinitiative.fenominal.core.impl.decorators.TokenDecoratorService;
import org.monarchinitiative.fenominal.core.impl.hpo.DefaultHpoMatcher;
import org.monarchinitiative.fenominal.core.impl.hpo.HpoConceptHit;
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
 * 5. Greedy match -- at any position i, choose the longest posible match and mask out the text accordingly.
 * @author Peter Robinson
 */
public class SimpleSentenceMapper implements SentenceMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleSentenceMapper.class);
    private final DefaultHpoMatcher hpoMatcher;
    private final TokenDecoratorService tokenDecoratorService;
    private final DecorationProcessorService decorationProcessorService;

    public SimpleSentenceMapper(DefaultHpoMatcher hpoMatcher, LexicalResources lexicalResources){
        this.hpoMatcher = hpoMatcher;
        this.tokenDecoratorService = new TokenDecoratorService(lexicalResources);
        this.decorationProcessorService = new DecorationProcessorService();
    }

    public List<DetailedMinedTerm> mapSentence(SimpleSentence ss) {
        List<SimpleToken> nonStopWords = ss.getTokens().stream()
                .filter(Predicate.not(token -> StopWords.isStop(token.getToken())))
                .collect(Collectors.toList());
        nonStopWords = this.tokenDecoratorService.decorate(nonStopWords);
        // key -- sentence start position, value -- candidate matches.
        Map<Integer, List<DetailedMinedTerm>> candidates = new HashMap<>();
        int LEN = Math.min(10, nonStopWords.size()); // check for maximum of 10 words TODO -- what is best option here?
        for (int i = 1; i <= LEN; i++) {
            Partition<SimpleToken> partition = new Partition<>(nonStopWords, i);
            for (List<SimpleToken> chunk : partition) {
                if (chunk.size() < i) {
                    continue; // last portion is smaller, we will get it in corresponding loop
                }
                List<String> stringchunk = chunk.stream().map(SimpleToken::getToken).collect(Collectors.toList());
                Optional<HpoConceptHit> opt = this.hpoMatcher.getMatch(stringchunk);
                double TODO_DWEFAULT_SIM = 1.0;
                if (opt.isPresent()) {
                    TermId hpoId = opt.get().hpoConcept().getHpoId();
                    DetailedMinedTerm mappedSentencePart =
                            decorationProcessorService.process(chunk, nonStopWords, hpoId, TODO_DWEFAULT_SIM);
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
        List<DetailedMinedTerm> mappedSentencePartList = new ArrayList<>();
        for (int i : startPosList) {
            if (i < currentSpan) {
                continue;
            }
            List<DetailedMinedTerm> candidatesAtPositionI = candidates.get(i);
            DetailedMinedTerm longest = getLongestPart(candidatesAtPositionI);
            mappedSentencePartList.add(longest);
            // advance to the last position of the current match
            // note that this is String position convention, and so the next hist could start at
            // currentSpan, but cannot be less than currentSpan without overlapping.
            currentSpan = longest.getEnd();
        }
        return mappedSentencePartList;
    }

    private DetailedMinedTerm getLongestPart(List<DetailedMinedTerm> candidatesAtPositionI) {
        // we should be guaranteed to have at least one list entry -- TODO do we need to check?
        DetailedMinedTerm max = candidatesAtPositionI.get(0);
        for (int i = 1; i < candidatesAtPositionI.size(); i++) {
            if (candidatesAtPositionI.get(i).getEnd() > max.getEnd()) {
                max = candidatesAtPositionI.get(i);
            }
        }
        return max;
    }
}
