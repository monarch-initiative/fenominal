package org.monarchinitiative.fenominal.corenlp;

public class SimpleSentence {

    private final String sentence;
    private final int start;
    private final int end;

    public SimpleSentence(String text, int start, int end) {
        this.sentence = text;
        this.start = start;
        this.end = end;
    }
}
