package org.monarchinitiative.fenominal.core.corenlp;


import org.monarchinitiative.fenominal.core.FenominalRunTimeException;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Split up a sentence in tokens. We extract works that optionally contain an apostrophe or dash
 * but that start with a letter. Transform the individual words into {@link SimpleToken} objects.
 */
public class SimpleSentence {

    private final String sentence;
    private final int start;
    private final int end;
    private final List<SimpleToken> tokens;
    /** Match words with optional non-initial apostrophe/dash. */
    private final static Pattern wordpattern = Pattern.compile("[a-zA-Z]+('-[a-zA-Z]+)?");

    public SimpleSentence(String text, int start, int end) {
        this.sentence = text;
        this.start = start;
        this.end = end;
        if (end-start != this.sentence.length()) {
            throw new FenominalRunTimeException(String.format("Expected length %d but got %d (=%d-%d) for sentence (\"%s\").",
                    sentence.length(), (end-start), end, start, sentence));
        }
        this.tokens = new ArrayList<>();
        Matcher matcher = wordpattern.matcher(this.sentence);
        while (matcher.find()) {
            SimpleToken token = new SimpleToken(matcher.group(), matcher.start(), matcher.end(), this.start);
            this.tokens.add(token);
        }
    }


    public List<SimpleToken> getTokens() {
        return this.tokens;
    }

    public String getText() {
        return this.sentence;
    }

    public String getSentence() {
        return sentence;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sentence, start, end);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SimpleSentence that)) return false;
        return this.sentence.equals(that.sentence) && this.start == that.start && this.end == that.end;
    }
}
