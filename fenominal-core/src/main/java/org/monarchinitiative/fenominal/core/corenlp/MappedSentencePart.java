package org.monarchinitiative.fenominal.core.corenlp;

import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MappedSentencePart {
    private final List<SimpleToken> tokens;
    private final TermId tid;
    private final Map<String, String> decorations;
    private final int startpos;
    private final int endpos;


    public MappedSentencePart(List<SimpleToken> tokens, TermId tid, Map<String, String> decorations) {
        this.tokens = tokens;
        this.tid = tid;
        this.decorations = decorations;
        this.startpos = tokens.get(0).getStartpos();
        this.endpos = tokens.get(tokens.size() - 1).getEndpos();
    }

    public String getMatchingString() {
        return tokens.stream().map(SimpleToken::getToken).collect(Collectors.joining(" "));
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

    public int getTokenCount() {
        return tokens.size();
    }

    @Override
    public String toString() {
        return String.format("%s (%s;%d-%d)", getMatchingString(), tid.getValue(), startpos, endpos);
    }
}
