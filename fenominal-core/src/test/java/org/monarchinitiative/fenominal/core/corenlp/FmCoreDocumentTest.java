package org.monarchinitiative.fenominal.core.corenlp;

import org.junit.jupiter.api.Test;
import org.monarchinitiative.fenominal.core.TestBase;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FmCoreDocumentTest extends TestBase {

    private final static FmCoreDocument doc1 = new FmCoreDocument(mytext1);
    private final static FmCoreDocument doc2 = new FmCoreDocument(mytext2);

    @Test
    public void if_get_three_sentences_then_ok() {
        assertEquals(3, doc1.getSentences().size());
    }


    @Test
    public void testShortSentences() {
        String text = "The quick. Brown fox. Jumps over. ";
        FmCoreDocument doc = new FmCoreDocument(text);
        assertEquals(3, doc.getSentences().size());
    }


    /**
     * SimpleSentence should not include trailing whitespace, but should include punctuation
     */
    @Test
    public void test_equality_of_sentences() {
        List<SimpleSentence> simpleSentences = doc1.getSentences();
        assertEquals(sentence1.trim(), simpleSentences.get(0).getText());
        assertEquals(sentence2.trim(), simpleSentences.get(1).getText());
        assertEquals(sentence3.trim(), simpleSentences.get(2).getText());
    }

    @Test
    public void if_get_four_sentences_then_ok() {
        assertEquals(4, doc2.getSentences().size());
    }

    @Test
    public void test_equality_of_sentences_doc2() {
        List<SimpleSentence> simpleSentences = doc2.getSentences();
        assertEquals(sentence4.trim(), simpleSentences.get(0).getText());
        assertEquals(sentence5.trim(), simpleSentences.get(1).getText());
        assertEquals(sentence6.trim(), simpleSentences.get(2).getText());
        assertEquals(sentence7.trim(), simpleSentences.get(3).getText());
    }




}
