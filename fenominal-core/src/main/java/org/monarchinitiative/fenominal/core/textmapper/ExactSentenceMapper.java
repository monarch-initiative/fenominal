package org.monarchinitiative.fenominal.core.textmapper;

import org.monarchinitiative.fenominal.model.MappedSentencePart;
import org.monarchinitiative.fenominal.core.corenlp.SimpleSentence;
import org.monarchinitiative.fenominal.core.corenlp.SimpleToken;
import org.monarchinitiative.fenominal.core.corenlp.StopWords;
import org.monarchinitiative.fenominal.core.decorators.DecorationProcessorService;
import org.monarchinitiative.fenominal.core.decorators.TokenDecoratorService;
import org.monarchinitiative.fenominal.core.hpo.HpoConceptHit;
import org.monarchinitiative.fenominal.core.hpo.HpoMatcher;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ExactSentenceMapper implements SentenceMapper {

    private final HpoMatcher hpoMatcher;
    private final TokenDecoratorService tokenDecoratorService;
    private final DecorationProcessorService decorationProcessorService;

    public ExactSentenceMapper(HpoMatcher hpoMatcher, TokenDecoratorService tokenDecoratorService, DecorationProcessorService decorationProcessorService) {
        this.hpoMatcher = hpoMatcher;
        this.tokenDecoratorService = tokenDecoratorService;
        this.decorationProcessorService = decorationProcessorService;
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
                    MappedSentencePart mappedSentencePart =
                            decorationProcessorService.process(chunk, nonStopWords, hpoId, 1.0);

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
            MappedSentencePart longest = TextMapperUtil.getLongestPart(candidatesAtPositionI);
            mappedSentencePartList.add(longest);
            // advance to the last position of the current match
            // note that this is String position convention, and so the next hist could start at
            // currentSpan, but cannot be less than currentSpan without overlapping.
            currentSpan = longest.getEnd();
        }
        return mappedSentencePartList;
    }

}
