package org.monarchinitiative.fenominal;

import org.monarchinitiative.fenominal.corenlp.MappedSentencePart;
import org.monarchinitiative.fenominal.corenlp.SimpleSentence;
import org.monarchinitiative.fenominal.corenlp.SimpleToken;
import org.monarchinitiative.fenominal.hpo.HpoLoader;
import org.monarchinitiative.fenominal.hpo.HpoMatcher;
import org.monarchinitiative.fenominal.hpo.SimpleHpoTerm;
import org.monarchinitiative.fenominal.textmapper.ClinicalTextMapper;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TextToHpoMapper {



    private final ClinicalTextMapper hpoMatcher;

    public TextToHpoMapper(String pathToHpObo) {
        hpoMatcher = new ClinicalTextMapper(pathToHpObo);
    }

    public synchronized List<MappedSentencePart>  mapText(String text) {
        return hpoMatcher.mapText(text);
    }


}
