package org.monarchinitiative.fenominal.core.impl.kmer;

import org.monarchinitiative.fenominal.core.impl.lexical.LexicalResources;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class TBlatUtil {

    public final static int INVERSION = 1;
    public final static int SHIFT = 2;
    public final static int SINGLE_REPLACEMENT = 3;
    public final static int GAP_SHIFT = 4;
    public final static int DOUBLE_REPLACEMENT = 5;
    public final static int NO_MATCH = 6;

    public final static Map<Integer, Double> TRANSITION_PROBABILITY = getTransitionMap();

    private final static Map<Integer, Double> getTransitionMap() {
        Map<Integer, Double> map = new LinkedHashMap<>();
        map.put(SHIFT, 0.42);
        map.put(SINGLE_REPLACEMENT, 0.20);
        map.put(DOUBLE_REPLACEMENT, 0.08);
        map.put(GAP_SHIFT, 0.07);
        map.put(INVERSION, 0.03);
        return map;
    }

    public static double avg_of_top_k(Map<String, Double> data, int k) {
        double sum = 0;
        int count = 0;

        for (String el : data.keySet()) {
            if (count == k) {
                break;
            }
            sum += data.get(el);
            count += 1;
        }
        return sum / count;
    }

    public static int detect_type_of_transition(String kmer, String typo) {
        char l0 = kmer.charAt(0);
        char l1 = kmer.charAt(1);
        char l2 = kmer.charAt(2);

        char t0 = typo.charAt(0);
        char t1 = typo.charAt(1);
        char t2 = typo.charAt(2);

        // met => ame | met => eta
        if ((l0 == t1 && l1 == t2) || (l1 == t0 && l2 == t1)) {
            return SHIFT;
        }

        // tar => atr | tar => tra
        if ((l0 == t1 && l1 == t0 && l2 == t2) || (l0 == t0 && l1 == t2 && l2 == t1)) {
            return INVERSION;
        }

        // tar => t_r | tar => _ar | tar => ta_
        if ((l0 == t0 && l2 == t2) || (l1 == t1 && l2 == t2) || (l0 == t0 && l1 == t1)) {
            return SINGLE_REPLACEMENT;
        }

        // tar => t_a | tar => a_r
        if ((l0 == t0 && l1 == t2) || (l1 == t0 && l2 == t2)) {
            return GAP_SHIFT;
        }

        // tar => t__ | tar => __r | tar => _a_
        if ((l0 == t0) || (l2 == t2) || (l1 == t1)) {
            return DOUBLE_REPLACEMENT;
        }

        return NO_MATCH;

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

    public static InputStream getResourceAsStream(String resourcePath) {
        InputStream is = LexicalResources.class.getResourceAsStream(resourcePath);
        if (is != null)
            return is;

        is = ClassLoader.getSystemClassLoader().getResourceAsStream(resourcePath);
        if (is != null)
            return is;

        // load from the module path
        Optional<Module> fenominalCoreOptional = ModuleLayer.boot().findModule("org.monarchinitiative.fenominal.core");
        if (fenominalCoreOptional.isPresent()) {
            Module fenominal = fenominalCoreOptional.get();
            try {
                return fenominal.getResourceAsStream(resourcePath);
            } catch (IOException e) {
                // swallow
            }
        }

        throw new IllegalStateException("Unable to load resource " + resourcePath);
    }

}
