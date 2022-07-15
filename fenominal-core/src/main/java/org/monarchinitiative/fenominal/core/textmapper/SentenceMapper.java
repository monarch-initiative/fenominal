package org.monarchinitiative.fenominal.core.textmapper;

import org.monarchinitiative.fenominal.model.MappedSentencePart;
import org.monarchinitiative.fenominal.core.corenlp.SimpleSentence;

import java.util.List;

public interface SentenceMapper {

    List<MappedSentencePart> mapSentence(SimpleSentence ss);
}
