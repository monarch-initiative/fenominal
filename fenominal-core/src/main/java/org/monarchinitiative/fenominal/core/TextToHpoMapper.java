package org.monarchinitiative.fenominal.core;

import org.monarchinitiative.fenominal.core.corenlp.MappedSentencePart;
import org.monarchinitiative.fenominal.core.lexical.LexicalResources;
import org.monarchinitiative.fenominal.core.textmapper.ClinicalTextMapper;
import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.ontology.data.Ontology;

import java.io.File;
import java.util.List;

/**
 * all public API calls for fenominal-core should go through this class
 */
public class TextToHpoMapper {

    private final ClinicalTextMapper hpoMatcher;

    private final LexicalResources lexicalResources;

    public TextToHpoMapper(String pathToHpJson) {
        Ontology hpo = OntologyLoader.loadOntology(new File(pathToHpJson));
        lexicalResources = new LexicalResources();
        hpoMatcher = new ClinicalTextMapper(hpo, lexicalResources);
    }

    public TextToHpoMapper(Ontology ontology) {
        lexicalResources = new LexicalResources();
        hpoMatcher = new ClinicalTextMapper(ontology, lexicalResources);
    }

    public synchronized List<MappedSentencePart> mapText(String text) {
        /**
         * TODO: Decide where to put the fuzzy flag !!!
         */
        List<MappedSentencePart> list = hpoMatcher.mapText(text, true);
        return list;
    }

    public static void main(String[] args) {
        TextToHpoMapper textToHpoMapper = new TextToHpoMapper("/home/tudor/dev/fenominal/fenominal-core/src/test/resources/hpo/hp.json");
        textToHpoMapper.mapText("Short finger and toes with trident hands, macrocephaly with prominent forehead frontal bossing.");
    }
}
