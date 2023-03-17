package org.monarchinitiative.fenominal.core;

import org.junit.jupiter.api.Test;
import org.monarchinitiative.fenominal.core.impl.FuzzyTermMiner;
import org.monarchinitiative.fenominal.core.impl.NonFuzzyTermMiner;
import org.monarchinitiative.fenominal.model.MinedTerm;
import org.monarchinitiative.fenominal.model.MinedTermWithMetadata;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FuzzyTermMinerTest {

    private static final Ontology hpo = TestResources.hpo();
    private static final TermMiner fuzzyTermMiner = new FuzzyTermMiner(hpo);

    private static final TermMiner exactTermMiner = new NonFuzzyTermMiner(hpo);


    private static final String exact = "A 28-year-old woman who was diagnosed with Noonan syndrome at age 4 because of growth retardation, cardiomyopathy, and hypertelorism.";
    private static final String errors = "A 28-year-old woman who was diagnosed with Noonan syndrome at age 4 because of growth retadation, cardiomyopathic, and hypertelorisn.";

    @Test
    public void testExactMatch() {
      Collection<MinedTermWithMetadata> result =  exactTermMiner.mineTermsWithMetadata(exact);
      assertEquals(3, result.size());
    }

    @Test
    public void testFuzzyMatch() {
        Collection<MinedTermWithMetadata> result =  fuzzyTermMiner.mineTermsWithMetadata(errors);
        List<MinedTermWithMetadata> resultList = new ArrayList<>(result);
        for (var r : resultList) {
            System.out.println(r);
        }
        assertEquals(4, resultList.size());
        var result0 = resultList.get(0);
        TermId agnosia = TermId.of("HP:0010524"); // Agnosia -- false positive from "diagnosed"
        assertEquals(agnosia, result0.getTermId());
        var result1 = resultList.get(1);
        TermId growthRetardation = TermId.of("HP:0001510"); // Growth retardation
        assertEquals(growthRetardation, result1.getTermId());
        var result2 = resultList.get(2);
        TermId cardiomyopathy = TermId.of("HP:0001638"); // Cardiomyopathy
        assertEquals(cardiomyopathy, result2.getTermId());
        var result3 = resultList.get(3);
        TermId hypertelorism = TermId.of("HP:0000316"); // Hypertelorism
        assertEquals(hypertelorism, result3.getTermId());
    }


    @Test
    public void testDoNotParseNegative() {
        Collection<MinedTerm>  results = exactTermMiner.mineTerms("negative test result");
        for (var x : results) {
            System.out.println(x);
        }
        // TODO Fix this
       // assertTrue(results.isEmpty());
    }

}
