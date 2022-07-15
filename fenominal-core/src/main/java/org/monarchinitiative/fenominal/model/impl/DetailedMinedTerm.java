package org.monarchinitiative.fenominal.model.impl;

import org.monarchinitiative.fenominal.core.impl.corenlp.SimpleToken;
import org.monarchinitiative.fenominal.core.impl.decorators.Decorations;
import org.monarchinitiative.fenominal.model.MinedTermWithMetadata;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DetailedMinedTerm implements MinedTermWithMetadata {
    private final List<SimpleToken> tokens;
    private final TermId tid;
    private final Map<String, String> decorations;
    private final int startpos;
    private final int endpos;
    private final double similarity;
    private final String matchingString;


    public DetailedMinedTerm(List<SimpleToken> tokens, TermId tid, double similarity, Map<String, String> decorations) {
        this.matchingString = tokens.stream().map(SimpleToken::getToken).collect(Collectors.joining(" "));
        this.tokens = tokens;
        this.tid = tid;
        this.similarity = similarity;
        this.decorations = decorations;
        this.startpos = tokens.get(0).getStartpos();
        this.endpos = tokens.get(tokens.size() - 1).getEndpos();
    }
    @Override
    public String getMatchingString() {
        return tokens.stream().map(SimpleToken::getToken).collect(Collectors.joining(" "));
    }

    public Map<String, String> getDecorations() {
        return decorations;
    }

    @Override
    public double getSimilarity() {
        return similarity;
    }

    @Override
    public String toString() {
        return String.format("%s (%s;%f;%d-%d)", matchingString, tid.getValue(), similarity, startpos, endpos);
    }
    @Override
    public int getTokenCount() {
        return this.tokens.size();
    }

    @Override
    public int getBegin() {
        return startpos;
    }

    @Override
    public int getEnd() {
        return endpos;
    }

    @Override
    public String getTermIdAsString() {
        return tid.getValue();
    }
    @Override
    public TermId getTermId() {
        return tid;
    }

    @Override
    public boolean isPresent() {
        return ! getDecorations().containsKey(Decorations.NEGATION.name());
    }
}
