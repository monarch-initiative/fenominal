package org.monarchinitiative.fenominal.textmapper;

import org.monarchinitiative.fenominal.corenlp.MappedSentencePart;
import org.monarchinitiative.fenominal.corenlp.SimpleSentence;
import org.monarchinitiative.fenominal.corenlp.SimpleToken;
import org.monarchinitiative.fenominal.corenlp.StopWords;
import org.monarchinitiative.fenominal.hpo.HpoConcept;
import org.monarchinitiative.fenominal.hpo.HpoMatcher;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ClinicalTextMapper {

    private final HpoMatcher hpoMatcher;

    public ClinicalTextMapper(String pathToHpObo) {
        this.hpoMatcher = new HpoMatcher(pathToHpObo);
    }

    public synchronized List<MappedSentencePart> mapText(String text) {
        List<SimpleSentence> sentences = SimpleSentence. splitInputSimple(text);
        List<MappedSentencePart> mappedParts = new ArrayList<>();
        for (var ss : sentences) {
            List<MappedSentencePart> sentenceParts = mapSentence(ss);
            mappedParts.addAll(sentenceParts);
        }
        return mappedParts;
    }

    private List<MappedSentencePart> mapSentence(SimpleSentence ss) {
        List<SimpleToken> nonStopWords = ss.getTokens().stream()
                .filter(Predicate.not(token ->  StopWords.isStop(token.getToken())))
                .collect(Collectors.toList());
        // key -- sentence start position, value -- candidate matches.
        Map<Integer, List<MappedSentencePart>> candidates = new HashMap<>();
        int LEN = Math.min(10,nonStopWords.size()); // check for maximum of 10 words TODO -- what is best option here?
        for (int i=1; i<=LEN;i++) {
            Partition<SimpleToken> partition = new Partition<>(nonStopWords, i);
            for (List<SimpleToken> chunk : partition) {
                if (chunk.size() < i) {
                    continue; // last portion is smaller, we will get it in corresponding loop
                }
                List<String> stringchunk = chunk.stream().map(SimpleToken::getToken).collect(Collectors.toList());
                Optional<HpoConcept> opt = this.hpoMatcher.getMatch(stringchunk);
                if (opt.isPresent()) {
                    MappedSentencePart mappedSentencePart = new MappedSentencePart(chunk, opt.get().getHpoId());
                    candidates.putIfAbsent(mappedSentencePart.getStartpos(), new ArrayList<>());
                    candidates.get(mappedSentencePart.getStartpos()).add(mappedSentencePart);
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
        }
        return mappedSentencePartList;
    }

    private MappedSentencePart getLongestPart(List<MappedSentencePart> candidatesAtPositionI) {
        // we should be guaranteed to have at least one list entry -- TODO do we need to check?
        MappedSentencePart max = candidatesAtPositionI.get(0);
        for (int i = 1; i<candidatesAtPositionI.size(); i++) {
            if (candidatesAtPositionI.get(i).getEndpos() > max.getEndpos()) {
                max = candidatesAtPositionI.get(i);
            }
        }
        return max;
    }


}
