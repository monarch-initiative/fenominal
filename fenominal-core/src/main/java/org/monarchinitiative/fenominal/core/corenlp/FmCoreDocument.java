package org.monarchinitiative.fenominal.core.corenlp;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Fenominal Core Document
 */
public class FmCoreDocument {


    private final int len;

    private final List<SimpleSentence> sentences;

    private final static Set<Character> sentenceEndPunctuation = Set.of('.', '!', '?');

    public FmCoreDocument(String text) {
        this.len = text.length();
        this.sentences = new ArrayList<>();
        int i = 0;
        int prev = 0;
        while (i<len) {
            char c = text.charAt(i);
            if (sentenceEndPunctuation.contains(c)) {
                if (i>prev) {
                    // skip empty sentences
                    String sentenceText = text.substring(prev, i+1).trim();
                    if (! sentenceText.isEmpty()) {
                        SimpleSentence ssentence = new SimpleSentence(sentenceText, prev, i+1);
                        this.sentences.add(ssentence);
                    }
                }
                // first skip to character following the punctuation and check that we are still in bounds
                ++i;
                // skip over ws
                while (i+1<len && Character.isSpaceChar(text.charAt(i))){
                    i++;
                }
                prev = i;
            }
            ++i;
        }
        if (prev < len) {
            String sentenceText = text.substring(prev, i).trim();
            if (! sentenceText.isEmpty()) {
                SimpleSentence ssentence = new SimpleSentence(sentenceText, prev, i);
                this.sentences.add(ssentence);
            }
        }
    }




    public List<SimpleSentence> getSentences() {
        return sentences;
    }
}
