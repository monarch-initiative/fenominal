package org.monarchinitiative.fenominal.core.textmapper;

import org.monarchinitiative.fenominal.core.corenlp.FmCoreDocument;
import org.monarchinitiative.fenominal.core.corenlp.SimpleSentence;

import java.util.List;

public class Vignette {
    /** A clinical vignette or other text we are mining. */
    private final String originalText;

    private final List<SimpleSentence> sentences;

    public Vignette(String text) {
        this.originalText = text;
        FmCoreDocument coreDocument = new FmCoreDocument(text);
        this.sentences = coreDocument.getSentences();
    }

    public String getOriginalText() {
        return originalText;
    }

    public List<SimpleSentence> getSentences() {
        return sentences;
    }
}
