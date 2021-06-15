package org.monarchinitiative.fenominal.core.corenlp;

import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.List;
import java.util.stream.Collectors;

public class MappedSentencePart {
    private final String matchingString;
    private final TermId tid;
    private final int startpos;
    private final int endpos;


    public MappedSentencePart(List<SimpleToken> tokens, TermId tid) {
        this.matchingString = tokens.stream().map(SimpleToken::getToken).collect(Collectors.joining(" "));
        this.tid = tid;
        this.startpos = tokens.get(0).getStartpos();
        this.endpos = tokens.get(tokens.size() -1).getEndpos();
    }

    public String getMatchingString() {
        return matchingString;
    }

    public TermId getTid() {
        return tid;
    }

    public int getStartpos() {
        return startpos;
    }

    public int getEndpos() {
        return endpos;
    }

    @Override
    public String toString() {
        return String.format("%s (%s;%d-%d)", matchingString, tid.getValue(), startpos, endpos);
    }
}
