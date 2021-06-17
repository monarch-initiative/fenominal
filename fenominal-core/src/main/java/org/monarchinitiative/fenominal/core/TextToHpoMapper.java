package org.monarchinitiative.fenominal.core;

import org.monarchinitiative.fenominal.core.corenlp.MappedSentencePart;
import org.monarchinitiative.fenominal.core.textmapper.ClinicalTextMapper;
import org.monarchinitiative.fenominal.json.JsonHpoParser;

import java.util.List;

/**
 * all public API calls for fenominal-core should go through this class
 */
public class TextToHpoMapper {



    private final ClinicalTextMapper hpoMatcher;

    public TextToHpoMapper(String pathToHpJson) {
        JsonHpoParser parser = new JsonHpoParser(pathToHpJson);
        hpoMatcher = new ClinicalTextMapper(parser.getHpo());
    }

    public synchronized List<MappedSentencePart>  mapText(String text) {
        return hpoMatcher.mapText(text);
    }


}
