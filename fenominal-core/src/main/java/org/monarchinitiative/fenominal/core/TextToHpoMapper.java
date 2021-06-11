package org.monarchinitiative.fenominal.core;

import org.monarchinitiative.fenominal.core.corenlp.MappedSentencePart;
import org.monarchinitiative.fenominal.core.textmapper.ClinicalTextMapper;

import java.util.List;


public class TextToHpoMapper {



    private final ClinicalTextMapper hpoMatcher;

    public TextToHpoMapper(String pathToHpObo) {
        hpoMatcher = new ClinicalTextMapper(pathToHpObo);
    }

    public synchronized List<MappedSentencePart>  mapText(String text) {
        return hpoMatcher.mapText(text);
    }


}
