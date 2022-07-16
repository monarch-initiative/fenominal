package org.monarchinitiative.fenominal.core.impl.textmapper;

import org.monarchinitiative.fenominal.model.impl.DetailedMinedTerm;
import org.monarchinitiative.fenominal.core.impl.corenlp.SimpleSentence;

import java.util.List;

public interface SentenceMapper {

    List<DetailedMinedTerm> mapSentence(SimpleSentence ss);
}
