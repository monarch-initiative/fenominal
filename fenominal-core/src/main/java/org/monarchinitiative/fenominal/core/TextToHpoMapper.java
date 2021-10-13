package org.monarchinitiative.fenominal.core;

import org.monarchinitiative.fenominal.core.corenlp.MappedSentencePart;
import org.monarchinitiative.fenominal.core.lexical.LexicalClustersBuilder;
import org.monarchinitiative.fenominal.core.textmapper.ClinicalTextMapper;
import org.monarchinitiative.fenominal.json.JsonHpoParser;
import org.monarchinitiative.phenol.ontology.data.Ontology;

import java.util.List;

/**
 * all public API calls for fenominal-core should go through this class
 */
public class TextToHpoMapper {

    private final ClinicalTextMapper hpoMatcher;

    private final LexicalClustersBuilder lexicalClustersBuilder;

    public TextToHpoMapper(String pathToHpJson) {
        JsonHpoParser parser = new JsonHpoParser(pathToHpJson);
        lexicalClustersBuilder = new LexicalClustersBuilder();
        hpoMatcher = new ClinicalTextMapper(parser.getHpo(), lexicalClustersBuilder);
    }

    public TextToHpoMapper(Ontology ontology) {
        lexicalClustersBuilder = new LexicalClustersBuilder();
        hpoMatcher = new ClinicalTextMapper(ontology, lexicalClustersBuilder);
    }

    public synchronized List<MappedSentencePart> mapText(String text) {
        return hpoMatcher.mapText(text);
    }

}
