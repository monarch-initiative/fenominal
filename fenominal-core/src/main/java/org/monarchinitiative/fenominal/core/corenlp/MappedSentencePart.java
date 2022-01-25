package org.monarchinitiative.fenominal.core.corenlp;

import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MappedSentencePart {
    private final String matchingString;
    private final TermId tid;
    private final Map<String, String> decorations;
    private final int startpos;
    private final int endpos;
    private final double similarity;


    public MappedSentencePart(List<SimpleToken> tokens, TermId tid, double similarity, Map<String, String> decorations) {
        this.matchingString = tokens.stream().map(SimpleToken::getToken).collect(Collectors.joining(" "));
        this.tid = tid;
        this.similarity = similarity;
        this.decorations = decorations;
        this.startpos = tokens.get(0).getStartpos();
        this.endpos = tokens.get(tokens.size() - 1).getEndpos();
    }

    public String getMatchingString() {
        return matchingString;
    }

    public TermId getTid() {
        return tid;
    }

    public Map<String, String> getDecorations() {
        return decorations;
    }

    public int getStartpos() {
        return startpos;
    }

    public int getEndpos() {
        return endpos;
    }

    public double getSimilarity() {
        return similarity;
    }

    @Override
    public String toString() {
        return String.format("%s (%s;%f;%d-%d)", matchingString, tid.getValue(), similarity, startpos, endpos);
    }
}
