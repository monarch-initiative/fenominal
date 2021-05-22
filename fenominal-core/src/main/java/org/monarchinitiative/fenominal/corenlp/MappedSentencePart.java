package org.monarchinitiative.fenominal.corenlp;

import org.monarchinitiative.phenol.ontology.data.TermId;

public class MappedSentencePart {
    private final String matchingString;
    private final TermId tid;
    private final int startpos;
    private final int endpos;


    public MappedSentencePart(SimpleToken token, TermId tid) {
        this.matchingString = token.getToken();
        this.tid = tid;
        this.startpos = token.getStartpos();
        this.endpos = token.getEndpos();
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
