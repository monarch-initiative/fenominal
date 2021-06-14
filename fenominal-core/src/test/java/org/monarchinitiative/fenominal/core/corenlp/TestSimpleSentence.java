package org.monarchinitiative.fenominal.core.corenlp;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestSimpleSentence {

    @Test
    public void testSentenceSplit() {
        String s1 = "The quick brown fox jumps over the lazy dog.";
        String s2 = "It is a sentence that contains all of the letters of the English alphabet.";
        String s3 = "Owing to its brevity and coherence, it has become widely known.";
        String mytext = s1 + " " + s2 + " " + s3;

        //System.out.println(s1.length());
        FmCoreDocument coreDocument = new FmCoreDocument(mytext);
        List<SimpleSentence> sentences = coreDocument.getSentences();
        assertEquals(3, sentences.size());
    }
}
