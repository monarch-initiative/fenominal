package org.monarchinitiative.fenominal.core.textmapper;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.fenominal.core.FenominalRunTimeException;
import org.monarchinitiative.fenominal.core.corenlp.MappedSentencePart;
import org.monarchinitiative.fenominal.core.lexical.LexicalResources;
import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.ontology.data.Ontology;

import java.io.File;
import java.net.URL;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClinicalTextMapperTest {


    private static ClinicalTextMapper mapper;

    @BeforeAll
    private static void init() {
        ClassLoader loader = ClinicalTextMapper.class.getClassLoader();
        URL url = loader.getResource("hpo/hp.json");
        if (url == null) {
            throw new FenominalRunTimeException("Could not find hp.json file");
        }
        File file = new File(url.getFile());
        Ontology ontology = OntologyLoader.loadOntology(file);
        LexicalResources lexicalResources = new LexicalResources();
        mapper = new ClinicalTextMapper(ontology, lexicalResources);
    }



    /**
     * 20-03-2020 -- instead of getting dolichocephalic head shape and broad nasal bridge and narrow palate,
     * Fenominal was applying "narrow" to "broad nasal bridge" and inferred Narrow nasal bridge
     * // Note -- with the version of the HP we are using for testing, dolichocephalic head shape is not an exact match
     * // However, broad nasal bridge is but it does not match
     */
    @Test
    public void testCommaProblem() {
        String query = "During her childhood, the association of early developmental delay, " +
                "intellectual impairment (IQ 65), macrocephaly and dysmorphisms (dolichocephalic " +
                "head shape, broad nasal bridge, narrow palate, brachydactyly, see Fig. 1A) raised " +
                "the suspicion of Sotos syndrome.";
        boolean doFuzzyMatch = false;
        List<MappedSentencePart> mappedSentenceParts = mapper.mapText(query, false);
        MappedSentencePart part1 = mappedSentenceParts.get(0);
        assertEquals(47, part1.getStartpos());
        assertEquals(66, part1.getEndpos());
        assertEquals("developmental delay", part1.getMatchingString());
        assertEquals("HP:0001263", part1.getTid().getValue());
        //intellectual impairment 68 91
        //intellectual impairment is synonym of Cognitive impairment HP:0100543
        MappedSentencePart part2 = mappedSentenceParts.get(1);
        assertEquals(68, part2.getStartpos());
        assertEquals(91, part2.getEndpos());
        assertEquals("intellectual impairment", part2.getMatchingString());
        assertEquals("HP:0100543", part2.getTid().getValue());
        //macrocephaly (HP:0000256;101-113)
        MappedSentencePart part3 = mappedSentenceParts.get(2);
        assertEquals(101, part3.getStartpos());
        assertEquals(113, part3.getEndpos());
        assertEquals("macrocephaly", part3.getMatchingString());
        assertEquals("HP:0000256", part3.getTid().getValue());
        assertEquals(6, mappedSentenceParts.size());

        MappedSentencePart part4 = mappedSentenceParts.get(3);

        MappedSentencePart part5 = mappedSentenceParts.get(4);
        System.out.println(part5.getMatchingString() +":"+ part5.getTid().getValue());

        // brachydactyly 195 208  HP:0001156
        MappedSentencePart part6 = mappedSentenceParts.get(5);
        assertEquals(195, part6.getStartpos());
        assertEquals(208, part6.getEndpos());
        assertEquals("brachydactyly", part6.getMatchingString());
        assertEquals("HP:0001156", part6.getTid().getValue());
        assertEquals(6, mappedSentenceParts.size());

    }


}