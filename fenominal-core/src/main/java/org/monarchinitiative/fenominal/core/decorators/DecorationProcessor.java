package org.monarchinitiative.fenominal.core.decorators;

import org.monarchinitiative.fenominal.core.corenlp.SimpleToken;

import java.util.List;

public interface DecorationProcessor {
    String getDecoration();

    String getProcessedValue(List<SimpleToken> chunk, List<SimpleToken> nonStopWords);
}
