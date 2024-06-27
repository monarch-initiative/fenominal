package org.monarchinitiative.fenominal.core;

import org.junit.jupiter.api.Test;
import org.monarchinitiative.fenominal.core.impl.NonFuzzyTermMiner;
import org.monarchinitiative.fenominal.model.MinedTermWithMetadata;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class NonfuzzyTextMapperTest {

    private static final Ontology hpo = TestResources.hpo();

    private static final TermMiner exactTermMiner = new NonFuzzyTermMiner(hpo);


    private static final String s1 = "hypoplastic ear lobes with cup-shaped right ear; anteverted nares";

    /**
     * The problem with our initial implementation
     * was that we were matching across semicolon. Thus,
     * "hypoplastic ear lobes with cup-shaped right ear; anteverted nares";
     * was matching the Term Anteverted ears (HP:0040080) instead of Anteverted nares (HP:0000463)
     */
    @Test
    public void testDoNotMatchAcrossSemicolon() {
        Collection<MinedTermWithMetadata> result =  exactTermMiner.mineTermsWithMetadata(s1);
        // Expect
        TermId hypoplasticEar = TermId.of("HP:0008551"); // Hypoplastic ear (0-15)
        TermId antevertedNares = TermId.of("HP:0000463"); //  Anteverted nares (49-65)")
        MinedTermWithMetadata mt1 = result.stream().filter(m -> m.getTermId().equals(hypoplasticEar)).findAny().orElseThrow();
        assertNotNull(mt1);
        assertEquals(hypoplasticEar, mt1.getTermId());
        assertEquals(0, mt1.getBegin());
        assertEquals(15, mt1.getEnd());
        MinedTermWithMetadata mt2 = result.stream().filter(m -> m.getTermId().equals(antevertedNares)).findAny().orElseThrow();
        assertNotNull(mt2);
        assertEquals(antevertedNares, mt2.getTermId());
        assertEquals(49, mt2.getBegin());
        assertEquals(65, mt2.getEnd());
        assertEquals(2, result.size());

    }


}
