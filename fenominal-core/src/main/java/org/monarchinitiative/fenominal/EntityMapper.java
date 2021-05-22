package org.monarchinitiative.fenominal;

import org.monarchinitiative.fenominal.corenlp.MappedSentencePart;
import org.monarchinitiative.fenominal.corenlp.SimpleSentence;
import org.monarchinitiative.fenominal.corenlp.SimpleToken;
import org.monarchinitiative.fenominal.hpo.SimpleHpoTerm;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EntityMapper {

    private final Map<String, TermId> textToTermMap;

    public EntityMapper(String pathToHpObo) {
        textToTermMap = SimpleHpoTerm.textToTermMap(pathToHpObo);
    }

    public synchronized List<MappedSentencePart>  mapText(String text) {
        List<SimpleSentence> sentences = SimpleSentence. splitInputSimple(text);
        List<MappedSentencePart> mappedParts = new ArrayList<>();
        for (SimpleSentence ss : sentences) {
            mappedParts.addAll(getMappedParts(ss));
        }
        return mappedParts;
    }

    private synchronized List<MappedSentencePart> getMappedParts(SimpleSentence ss) {
        // first pass
        List<MappedSentencePart> mappedParts = new ArrayList<>();
        for (SimpleToken st : ss.getTokens()) {
            if (this.textToTermMap.containsKey(st.getLowerCaseToken())) {
                MappedSentencePart mspart = new MappedSentencePart(st, this.textToTermMap.get(st.getToken()));
                mappedParts.add(mspart);
            }
        }
        return mappedParts;
    }

}
