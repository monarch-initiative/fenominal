package org.monarchinitiative.fenominal.core.impl.textmapper;

import org.monarchinitiative.fenominal.core.impl.corenlp.SimpleToken;
import org.monarchinitiative.fenominal.core.impl.decorators.DecorationProcessorService;
import org.monarchinitiative.fenominal.core.impl.decorators.TokenDecoratorService;
import org.monarchinitiative.fenominal.core.impl.hpo.HpoConceptHit;
import org.monarchinitiative.fenominal.core.impl.hpo.HpoMatcher;
import org.monarchinitiative.fenominal.model.impl.DetailedMinedTerm;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractSentenceMapper implements SentenceMapper {

    protected final HpoMatcher hpoMatcher;
    protected final TokenDecoratorService tokenDecoratorService;
    protected final DecorationProcessorService decorationProcessorService;

    public AbstractSentenceMapper(HpoMatcher hpoMatcher, TokenDecoratorService tokenDecoratorService,
                                  DecorationProcessorService decorationProcessorService) {
        this.hpoMatcher = hpoMatcher;
        this.tokenDecoratorService = tokenDecoratorService;
        this.decorationProcessorService = decorationProcessorService;
    }

    public List<DetailedMinedTerm> mapSentence(List<SimpleToken> tokens) {
        // key -- sentence start position, value -- candidate matches.
        Map<Integer, List<DetailedMinedTerm>> candidates = new HashMap<>();
        int LEN = Math.min(10, tokens.size()); // check for maximum of 10 words TODO -- what is best option here?
        for (int i = 1; i <= LEN; i++) {
            Partition<SimpleToken> partition = new Partition<>(tokens, i);
            for (List<SimpleToken> chunk : partition) {
                if (chunk.size() < i) {
                    continue; // last portion is smaller, we will get it in corresponding loop
                }
                List<String> stringchunk = chunk.stream().map(SimpleToken::getToken).collect(Collectors.toList());
                Optional<HpoConceptHit> opt = this.hpoMatcher.getMatch(stringchunk);
                if (opt.isPresent()) {
                    TermId hpoId = opt.get().hpoConcept().getHpoId();
                    DetailedMinedTerm mappedSentencePart =
                            decorationProcessorService.process(chunk, tokens, hpoId, 1.0);
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
