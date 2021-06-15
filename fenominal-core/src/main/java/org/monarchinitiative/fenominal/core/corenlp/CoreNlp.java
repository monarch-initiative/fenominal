package org.monarchinitiative.fenominal.core.corenlp;


import java.util.ArrayList;
import java.util.List;

public class CoreNlp {


    public void splitInputSimple(String input) {
        List<SimpleSentence> sentences = new ArrayList<>();
        //Document doc = new Document(input);
        FmCoreDocument doc = new FmCoreDocument(input);
//        for (Sentence sentence : doc.sentences()) {
//            System.out.println(sentence.toString());
//            int start = sentence.characterOffsetBegin(0);
//            int end = sentence.characterOffsetEnd(sentence.length() - 1);
//            SimpleSentence ss = new SimpleSentence(sentence.toString(), start, end);
//            sentences.add(ss);
//        }
    }



}
