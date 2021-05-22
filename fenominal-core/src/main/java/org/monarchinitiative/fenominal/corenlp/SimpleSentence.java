package org.monarchinitiative.fenominal.corenlp;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;



import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


public class SimpleSentence {

    private final String sentence;
    private final int start;
    private final int end;
    private final List<SimpleToken> tokens;

    private final static Properties props = new Properties();

    static {
        props.setProperty("annotators","tokenize");
        props.setProperty("tokenize.options","splitHyphenated=false,americanize=false");
    }

    private final static StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

    public SimpleSentence(String text, int start, int end) {
        this.sentence = text;
        this.start = start;
        this.end = end;
        CoreDocument doc = new CoreDocument(this.sentence);
        pipeline.annotate(doc);
        this.tokens = new ArrayList<>();
        for (CoreLabel tok : doc.tokens()) {
            SimpleToken stoken = new SimpleToken(tok.word(), tok.beginPosition(), tok.endPosition(), this.start);
            this.tokens.add(stoken);
        }
    }

    public List<SimpleToken> getTokens() {
        return this.tokens;
    }



    public static List<SimpleSentence> splitInputSimple(String input) {
        List<SimpleSentence> sentences = new ArrayList<>();
        Document doc = new Document(input);
        for (Sentence sentence : doc.sentences()) {
            System.out.println(sentence.toString());
            int start = sentence.characterOffsetBegin(0);
            int end = sentence.characterOffsetEnd(sentence.length() - 1);
            SimpleSentence ss = new SimpleSentence(sentence.toString(), start, end);
            sentences.add(ss);
        }
        return sentences;
    }


}
