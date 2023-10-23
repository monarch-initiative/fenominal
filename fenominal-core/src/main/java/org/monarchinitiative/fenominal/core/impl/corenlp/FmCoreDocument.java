package org.monarchinitiative.fenominal.core.impl.corenlp;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Fenominal Core Document
 */
public class FmCoreDocument {

    private final String originalText;

    private final List<SimpleSentence> sentences;

    private final static Set<Character> sentenceEndPunctuation = Set.of('.', '!', '?');
    /** We call these punctuation marks "breaking" because we do not want to match text across such marks to one HPO term.*/
    private final static Set<Character> breakingPunctuation = Set.of(';', ':', '.', '!', '?');

    public FmCoreDocument(String text) {
        this.originalText = text;
        int len = this.originalText.length();
        this.sentences = new ArrayList<>();
        // get positions of sentence-end punctuations
        List<Integer> positions = getPunctuationPositions();
        // get fragments between the positions
        // do not bother to throw out whitespace, which will not be a problem downstream
        int i = 0;
        for (int endpos : positions) {
            String sentenceText = this.originalText.substring(i, endpos);
            if (i>=endpos || sentenceText.trim().isEmpty()) {
                continue; // skip too short fragments
            }
            SimpleSentence ssentence = new SimpleSentence(sentenceText, i, endpos);
            this.sentences.add(ssentence);
            // move past any white space that follows the punctuation
            i = endpos;
            while (i<len && Character.isSpaceChar(text.charAt(i))) i++;
        }
    }

    private List<Integer> getPunctuationPositions() {
        int len = this.originalText.length();
        List<Integer> positions = new ArrayList<>();
        int i = 0;
        while (i<len) {
            if (breakingPunctuation.contains(this.originalText.charAt(i))) {
                positions.add(i+1); // add position after punctuation, to include it in substring
            }
            i++;
        }
        // always add the last position (which possibly contains a punctuation
        // mark or could be whitepaace or something else).
        positions.add(len);
        return positions;
    }


    public List<SimpleSentence> getSentences() {
        return sentences;
    }
}
