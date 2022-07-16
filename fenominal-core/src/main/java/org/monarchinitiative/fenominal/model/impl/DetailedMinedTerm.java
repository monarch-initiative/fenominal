package org.monarchinitiative.fenominal.model.impl;

import org.monarchinitiative.fenominal.core.impl.corenlp.SimpleToken;
import org.monarchinitiative.fenominal.core.impl.decorators.Decoration;
import org.monarchinitiative.fenominal.model.MinedTermWithMetadata;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DetailedMinedTerm implements MinedTermWithMetadata {
    private final List<SimpleToken> tokens;
    private final TermId tid;
    private final Map<Decoration, String> decorations;
    private final int startpos;
    private final int endpos;
    private final double similarity;
    private final String matchingString;
    /** If true, term was observed; if false, term was excluded */
    private final boolean isPresent;


    public DetailedMinedTerm(List<SimpleToken> tokens, TermId tid, double similarity, Map<Decoration, String> decorations) {
        this.matchingString = tokens.stream().map(SimpleToken::getToken).collect(Collectors.joining(" "));
        this.tokens = tokens;
        this.tid = tid;
        this.similarity = similarity;
        if (! decorations.containsKey(Decoration.NEGATION)) {
            this.isPresent = true;
        } else {
            String value = decorations.get(Decoration.NEGATION);
            if (value.equalsIgnoreCase("true")) {
                this.isPresent = false;
            } else {
                this.isPresent = true;
            }
        }
        this.decorations = decorations;
        this.startpos = tokens.get(0).getStartpos();
        this.endpos = tokens.get(tokens.size() - 1).getEndpos();
    }
    @Override
    public String getMatchingString() {
        return tokens.stream().map(SimpleToken::getToken).collect(Collectors.joining(" "));
    }

    public Map<Decoration, String> getDecorations() {
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
        if (! getDecorations().containsKey(Decoration.NEGATION)) {
            return true;
        }
        return getDecorations().get(Decoration.NEGATION).equalsIgnoreCase("false");
    }
}
