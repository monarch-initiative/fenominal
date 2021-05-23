package org.monarchinitiative.fenominal.textmapper;

import org.monarchinitiative.fenominal.corenlp.MappedSentencePart;
import org.monarchinitiative.fenominal.corenlp.SimpleSentence;
import org.monarchinitiative.fenominal.corenlp.SimpleToken;
import org.monarchinitiative.fenominal.corenlp.StopWords;
import org.monarchinitiative.fenominal.except.FenominalRunTimeException;
import org.monarchinitiative.fenominal.hpo.HpoConcept;
import org.monarchinitiative.fenominal.hpo.HpoMatcher;
import org.monarchinitiative.phenol.ontology.data.TermId;

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
        List<String> nonStopWords = ss.getTokens().stream()
                .map(SimpleToken::getToken)
                .filter(Predicate.not(w ->  StopWords.isStop(w)))
                .collect(Collectors.toList());
        // key -- sentence start position, value -- candidate matches.
        Map<Integer, List<MappedSentencePart>> candidates = new HashMap<>();
        int LEN = Math.min(10,nonStopWords.size());
        for (int i=1; i<=LEN;i++) {
            Partition partition = new Partition(nonStopWords, i);
            int size = partition.size();
            for (int j=0;j<size;j++) {
                List<String> chunk = partition.get(j);
                if (chunk.size() < i) {
                    continue; // last portion is smaller, we will get it in corresponding loop
                }
                Optional<HpoConcept> opt = this.hpoMatcher.getMatch(chunk);
                if (opt.isPresent()) {
                    MappedSentencePart mappedSentencePart = getMappedSentencePart(opt.get().getHpoId(), chunk, ss);
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

    Optional<Integer> findIndexOfFirstMatch(String token, int startIndex, List<SimpleToken> tokenList) {
        for (int i=startIndex;i<tokenList.size();i++) {
            if (token.equals(tokenList.get(i).getToken())) {
                return Optional.of(i);
            }
        }
        return Optional.empty();
    }

    /**
     * Figure out where the chunk we just found is in the original sentence
     * @param tid
     * @param chunk
     * @param ss
     * @return
     */
    private MappedSentencePart getMappedSentencePart(TermId tid, List<String> chunk, SimpleSentence ss) {
        List<SimpleToken> tokens = ss.getTokens();
        int start = -1, end = -1;
        int i=0;
        Optional<Integer> opt = findIndexOfFirstMatch(chunk.get(0), 0, tokens);
        if (! opt.isPresent()) {
            // should never happen
            throw new FenominalRunTimeException("Cound not find chunk(0)");
        }
        start = opt.get();
        if (chunk.size() == 1) {
            // single word match is easy
            SimpleToken stoken = tokens.get(start);
            return new MappedSentencePart(List.of(stoken), tid);
        }


        i = start + 1;
        int chunkIdx = 1;
        while (i < tokens.size() && chunkIdx < chunk.size()) {
            opt = findIndexOfFirstMatch(chunk.get(chunkIdx), i, tokens);
            if (! opt.isPresent()) {
                // should never happen
                throw new FenominalRunTimeException("Could not find chunk(" + i + ") for " + ss.toString());
            } else {
                end = opt.get(); // current last token
                i=end+1;
                chunkIdx++;
            }
        }
        if (chunkIdx == chunk.size()) {
            return new MappedSentencePart(tokens.subList(start, end), tid);
        } else {
            throw new FenominalRunTimeException("Could not mapp");
        }
    }

}
