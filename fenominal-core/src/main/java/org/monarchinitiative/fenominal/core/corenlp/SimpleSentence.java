package org.monarchinitiative.fenominal.core.corenlp;


import org.monarchinitiative.fenominal.core.FenominalRunTimeException;

import java.util.*;


public class SimpleSentence {

    private final String sentence;
    private final int start;
    private final int end;
    private final List<SimpleToken> tokens;

    private final static Set<Character> sentenceEndPunctuation = Set.of('.', '!', '?');


    public SimpleSentence(String text, int start, int end) {
        this.sentence = text;
        this.start = start;
        this.end = end;
        if (end-start  != this.sentence.length()) {
            throw new FenominalRunTimeException(String.format("Incompatible start (%d) and end (%d) for sentence (\"%s\") with length %d",
                    start, end, sentence, sentence.length()));
        }
        this.tokens = new ArrayList<>();
        int i = 0;

        int len = text.length();
        // move up to first non-whitespace
        while(i < len && Character.isSpaceChar(text.charAt(i))) {
            i++;
        }
        int prev = i;
        while (i<len) {
            char c = text.charAt(i);
            if (Character.isSpaceChar(c) || sentenceEndPunctuation.contains(c)) {
                if (i > prev) {
                    SimpleToken stoken = new SimpleToken(text
                            .substring(prev, i), prev, i, this.start);
                    this.tokens.add(stoken);
                    prev = i + 1;
                }
                // skip over ws
                while (i + 1 < len && Character.isSpaceChar(text.charAt(i + 1))) {
                    i++;
                }

            }
            ++i;
        }
        if (prev < len) {
            String tokenText = text.substring(prev, i);
            if (! tokenText.trim().isEmpty()) {
                SimpleToken stoken = new SimpleToken(tokenText, prev, i, start);
                this.tokens.add(stoken);
            }
        }
    }

    public List<SimpleToken> getTokens() {
        return this.tokens;
    }

    public String getText() {
        return this.sentence;
    }



}
