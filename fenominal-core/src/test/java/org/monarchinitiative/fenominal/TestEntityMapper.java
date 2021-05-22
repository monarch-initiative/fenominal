package org.monarchinitiative.fenominal;

import org.junit.jupiter.api.Test;
import org.monarchinitiative.fenominal.corenlp.MappedSentencePart;

import java.util.List;

public class TestEntityMapper {

    private final static String clinicalVignette =
    "A 28-year-old woman who was diagnosed with Noonan syndrome at age 4 " +
    "because of growth retardation, cardiomyopathy, and facial features. " +
    "At age 6.5 years she was diagnosed with partial growth hormone deficiency" +
    "and was treated with growth hormone. Her psychomotor development was normal. " +
    "She was noted to have a broad forehead, hypertelorism, downslanting palpebral " +
    "fissures, bilateral ptosis, a short and broad neck with a low hairline, and " +
    "low-set ears with broad helices. She had cafe-au-lait spots on her back and " +
    "many lentigines all over her body.";

    @Test
    public void testNer() {
        String hpo="/home/peter/GIT/human-phenotype-ontology/hp.obo";
        EntityMapper mapper = new EntityMapper(hpo);
        List<MappedSentencePart>  mappedSentenceParts = mapper.mapText(clinicalVignette);
        for (var mp : mappedSentenceParts) {
            System.out.println(mp);
        }
        System.out.printf("We got %d parts.\n", mappedSentenceParts.size());
    }
}
