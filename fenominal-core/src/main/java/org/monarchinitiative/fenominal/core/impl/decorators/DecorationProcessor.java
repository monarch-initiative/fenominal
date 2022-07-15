package org.monarchinitiative.fenominal.core.impl.decorators;

import org.monarchinitiative.fenominal.core.impl.corenlp.SimpleToken;

import java.util.List;

public interface DecorationProcessor {
    String getDecoration();

    String getProcessedValue(List<SimpleToken> chunk, List<SimpleToken> nonStopWords);
}
