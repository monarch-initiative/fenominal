package org.monarchinitiative.fenominal.core.corenlp;

import org.junit.jupiter.api.Test;
import org.monarchinitiative.fenominal.core.FenominalRunTimeException;
import org.monarchinitiative.fenominal.core.TestBase;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SimpleSentenceTest extends TestBase {
    /** The quick brown fox jumps over the lazy dog. " */
    private static final SimpleSentence ssentence1 = new SimpleSentence(TestBase.sentence1, 0, 45);
    /** "Quam nihil molestiae consequatur, vel illum, qui dolorem eum fugiat. "; */
    private static final SimpleSentence ssentence7 = new SimpleSentence(sentence7, 0, 69);


    /**
     * Length of the sentence is 45, and thus start=0 and end=15 is an error
     */
    @Test
    public void testSentenceSplit() {
        FenominalRunTimeException thrown = assertThrows(
                FenominalRunTimeException.class, () -> {
                    SimpleSentence sentence = new SimpleSentence(TestBase.sentence1, 0, 15);
                });
    }

    @Test
    public void testTextEquality() {
        assertEquals(sentence1, ssentence1.getText());
    }

    @Test
    public void if_obtain_nine_tokens_then_ok() {
        assertEquals(9, ssentence1.getTokens().size());
    }

    @Test
    public void testEqualityOfTokens() {
        List<SimpleToken> tokens = ssentence1.getTokens();
        assertEquals("The", tokens.get(0).getToken());
        assertEquals("quick", tokens.get(1).getToken());
        assertEquals("brown", tokens.get(2).getToken());
        assertEquals("fox", tokens.get(3).getToken());
        assertEquals("jumps", tokens.get(4).getToken());
        assertEquals("over", tokens.get(5).getToken());
        assertEquals("the", tokens.get(6).getToken());
        assertEquals("lazy", tokens.get(7).getToken());
        assertEquals("dog", tokens.get(8).getToken());
    }
}
