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
        return hpoMatcher.mapText(text);
    }

}
