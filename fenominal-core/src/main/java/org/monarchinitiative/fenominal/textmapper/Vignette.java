package org.monarchinitiative.fenominal.textmapper;

import org.monarchinitiative.fenominal.corenlp.SimpleSentence;

import java.util.List;

public class Vignette {
    /** A clinical vignette or other text we are mining. */
    private final String originalText;

    private final List<SimpleSentence> sentences;

    public Vignette(String text) {
        this.originalText = text;
        this.sentences = SimpleSentence. splitInputSimple(text);
    }

    public String getOriginalText() {
        return originalText;
    }

    public List<SimpleSentence> getSentences() {
        return sentences;
    }
}
