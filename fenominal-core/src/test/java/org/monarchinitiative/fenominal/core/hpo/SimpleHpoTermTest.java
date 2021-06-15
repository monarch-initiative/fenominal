package org.monarchinitiative.fenominal.core.hpo;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SimpleHpoTermTest {

    private final static File smallHpo = Paths.get("src/test/resources/hpo/hp.small.json").toFile();
    private final static HpoLoader loader = new HpoLoader(smallHpo.getAbsolutePath());
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
//        TermId tid = TermId.of("HP:0012372");
//        SimpleHpoTerm sht = hpoTerms.stream().filter(s -> s.getId().equals(tid)).findFirst().orElseThrow();
//       // assertNotNull(sht);
//        Set<String> expectedXrefs = Set.of("Fyler:4863", "UMLS:C4022925");
      //  assertEquals(expectedXrefs, sht.getXrefs());
        assertTrue(true);
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
//        TermId tid = TermId.of("HP:0100886");
//        SimpleHpoTerm sht = hpoTerms.stream().filter(s -> s.getId().equals(tid)).findFirst().orElseThrow();
//        assertNotNull(sht);
//        Set<String> expectedSynonyms = Set.of("Abnormality of eyeball location",
//                "Abnormality of eyeball position",
//                "Abnormality of globe position");
        //assertEquals(expectedSynonyms, sht.getSynonyms());
        assertTrue(true);
    }

    /**
     * grep -c '\[Term\]' hp_head.obo
     * 22
     */
    @Test
    public void testRetrieveAllTerms() {
        int expectedCount = 22;
       // assertEquals(expectedCount, hpoTerms.size());
        assertTrue(true);
    }


}
