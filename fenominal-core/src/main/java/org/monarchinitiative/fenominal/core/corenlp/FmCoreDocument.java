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

    private final Set<Character> sentenceEndPunctuation = Set.of('.', '!', '?');

    public FmCoreDocument(String text) {
        this.len = text.length();
        this.sentences = new ArrayList<>();
        int i = 0;
        int prev = 0;
        while (i<len) {
            char c = text.charAt(i);
            if (sentenceEndPunctuation.contains(c)) {
                if (i>prev) {
                    SimpleSentence ssentence = new SimpleSentence(text
                    .substring(prev, i), prev, i);
                    this.sentences.add(ssentence);
                    prev = i+1;
                }
                // skip over ws
                while (i+1<len && Character.isSpaceChar(text.charAt(i+1))){
                    i++;
                }
            }
            ++i;
        }
        if (prev < len) {
            SimpleSentence ssentence = new SimpleSentence(text
                    .substring(prev, i), prev, i);
            this.sentences.add(ssentence);
        }
    }




    public List<SimpleSentence> getSentences() {
        return sentences;
    }
}
