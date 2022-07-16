package org.monarchinitiative.fenominal.core.impl.corenlp;

import java.util.Set;

public class StopWords {

    private final static Set<String> stop = Set.of("a", "the", "and","of", "in", "to", "on", "an", "with");



    public static boolean isStop(String word) {
        return stop.contains(word.toLowerCase());
    }

}
