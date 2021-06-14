package org.monarchinitiative.fenominal.core.corenlp;


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

    //private final static StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

    public SimpleSentence(String text, int start, int end) {
        this.sentence = text;
        this.start = start;
        this.end = end;
        //CoreDocument doc = new CoreDocument(this.sentence);
        //pipeline.annotate(doc);
        this.tokens = new ArrayList<>();
//        for (CoreLabel tok : doc.tokens()) {
//            SimpleToken stoken = new SimpleToken(tok.word(), tok.beginPosition(), tok.endPosition(), this.start);
//            this.tokens.add(stoken);
//        }
        int i = 0;

        int len = text.length();
        // move up to first non-whitespace
        while(Character.isSpaceChar(text.charAt(i))) {
            i++;
        }
        int prev = i;
        while (i<len) {
            char c = text.charAt(i);
            if (Character.isSpaceChar(c)) {
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
            SimpleToken stoken  = new SimpleToken(text
                    .substring(prev, i), prev, i, start);
            this.tokens.add(stoken);
        }
    }

    public List<SimpleToken> getTokens() {
        return this.tokens;
    }





}
