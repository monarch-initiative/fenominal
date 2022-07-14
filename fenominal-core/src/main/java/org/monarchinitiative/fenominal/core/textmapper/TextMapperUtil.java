package org.monarchinitiative.fenominal.core.textmapper;

import org.monarchinitiative.fenominal.core.corenlp.MappedSentencePart;

import java.util.ArrayList;
import java.util.List;

public class TextMapperUtil {

    public static MappedSentencePart getLongestPart(List<MappedSentencePart> candidatesAtPositionI) {
        // we should be guaranteed to have at least one list entry -- TODO do we need to check?
        MappedSentencePart max = candidatesAtPositionI.get(0);
        for (int i = 1; i < candidatesAtPositionI.size(); i++) {
            if (candidatesAtPositionI.get(i).getEnd() > max.getEnd()) {
                max = candidatesAtPositionI.get(i);
            }
        }
        return max;
    }

    public static List<String> kmers(String token, int k) {
        if (token.length() <= k) { // cannot extract kmers sice String is not more than k characters
            return List.of(token);
        }
        List<String> list = new ArrayList<>();
        for (int i = 0; i < token.length() - k + 1; i++) {
            list.add(token.substring(i, i + k));
        }
        return list;
    }

    public static double setBigramRatio(String str1, String str2) {
        List<String> pairs1 = toBigrams(str1);
        List<String> pairs2 = toBigrams(str2);
        int union = pairs1.size() + pairs2.size();
        double hit_count = 0;
        for (String x : pairs1) {
            for (String y : pairs2) {
                if (x.equalsIgnoreCase(y)) {
                    hit_count++;
                    break;
                }
            }
        }
        return (2.0 * hit_count) / union;
    }

    public static List<String> toBigrams(String str) {
        List<String> result = new ArrayList<>();
        String s = str.toLowerCase();
        for (int i = 0; i < str.length() - 1; i++) {
            result.add(str.substring(i, i + 2));
        }
        return result;
    }
}
