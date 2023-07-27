package org.monarchinitiative.fenominal.core.hpo;

import org.junit.jupiter.api.Test;
import org.monarchinitiative.fenominal.core.TestResources;
import org.monarchinitiative.fenominal.core.impl.hpo.HpoLoader;
import org.monarchinitiative.fenominal.core.impl.hpo.SimpleHpoTerm;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class SimpleHpoTermTest {

    private final static HpoLoader loader = new HpoLoader(TestResources.smallHpo());
    private final List<SimpleHpoTerm> hpoTerms = loader.loadSimpleHpoTerms();


    /**
     * HP:0012372
     * name: Abnormal eye morphology
     * def: "A structural anomaly of the eye." [HPO:probinson]
     * synonym: "Abnormal eye morphology" EXACT layperson []
     * synonym: "Abnormally shaped eye" EXACT layperson [ORCID:0000-0001-5208-3432]
     * xref: Fyler:4863
     * xref: UMLS:C4022925
     */
    @Test
    public void testXref() {
        TermId tid = TermId.of("HP:0012372");
        SimpleHpoTerm sht = hpoTerms.stream().filter(s -> s.getId().equals(tid)).findFirst().orElseThrow();
        assertNotNull(sht);
        Set<String> expectedXrefs = Set.of("Fyler:4863", "UMLS:C4022925");
        assertEquals(expectedXrefs, sht.getXrefs());
    }

    /**
     * [Term]
     * id: HP:0100886
     * name: Abnormality of globe location
     * def: "An abnormality in the placement of the ocular globe (eyeball)." [HPO:sdoelken]
     * synonym: "Abnormality of eyeball location" EXACT layperson [ORCID:0000-0001-5889-4463]
     * synonym: "Abnormality of eyeball position" EXACT layperson [ORCID:0000-0001-5889-4463]
     * synonym: "Abnormality of globe position" EXACT [ORCID:0000-0001-5889-4463]
     * xref: UMLS:C4021946
     * is_a: HP:0012374 ! Abnormal globe morphology
     * created_by: doelkens
     * creation_date: 2011-12-13T04:25:29Z
     */
    @Test
    public void testSynonyms() {
        TermId tid = TermId.of("HP:0100886");
        SimpleHpoTerm sht = hpoTerms.stream().filter(s -> s.getId().equals(tid)).findFirst().orElseThrow();
        assertNotNull(sht);
        Set<String> expectedSynonyms = Set.of("Abnormality of eyeball location",
                "Abnormality of eyeball position",
                "Abnormality of globe position");
        assertEquals(expectedSynonyms, sht.getSynonyms());
    }

    /**
     * grep \"id\" hp_head.json | grep HP | wc -l
     *       22
     *  But HP_0012374 is deprecated=true, so we expect 21
     *  However, we are restricting to descendents of Phenotypic abnormality so we only get 10
     */
    @Test
    public void testRetrieveAllTerms() {
        assertEquals(9, hpoTerms.size());
    }


}
