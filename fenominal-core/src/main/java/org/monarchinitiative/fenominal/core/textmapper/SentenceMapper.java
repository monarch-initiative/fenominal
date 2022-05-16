package org.monarchinitiative.fenominal.core.textmapper;

import org.monarchinitiative.fenominal.core.corenlp.MappedSentencePart;
import org.monarchinitiative.fenominal.core.corenlp.SimpleSentence;

import java.util.List;

public interface SentenceMapper {

    List<MappedSentencePart> mapSentence(SimpleSentence ss);
}
