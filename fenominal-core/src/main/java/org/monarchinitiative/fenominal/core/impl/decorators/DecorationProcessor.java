package org.monarchinitiative.fenominal.core.impl.decorators;

import org.monarchinitiative.fenominal.core.impl.corenlp.SimpleToken;

import java.util.List;

public interface DecorationProcessor {
    Decoration getDecoration();

    String getProcessedValue(List<SimpleToken> chunk, List<SimpleToken> nonStopWords);
}
