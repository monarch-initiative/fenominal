package org.monarchinitiative.fenominal.core.decorators.impl;

import org.monarchinitiative.fenominal.core.corenlp.SimpleToken;
import org.monarchinitiative.fenominal.core.decorators.DecorationProcessor;
import org.monarchinitiative.fenominal.core.decorators.Decorations;

import java.util.List;

public class NegationDecorationProcessor implements DecorationProcessor {

    public NegationDecorationProcessor() {

    }

    @Override
    public String getDecoration() {
        return Decorations.NEGATION.name();
    }

    @Override
    public String getProcessedValue(List<SimpleToken> chunk, List<SimpleToken> nonStopWords) {
        if (chunk.isEmpty()) {
            return Boolean.FALSE.toString();
        }

        int negPosition = Integer.MAX_VALUE;
        for (SimpleToken simpleToken : nonStopWords) {
            if (simpleToken.hasDecoration(Decorations.NEGATION.name())) {
                negPosition = simpleToken.getStartpos();
                break;
            }
        }

        return chunk.get(0).getStartpos() > negPosition ?  Boolean.TRUE.toString() : Boolean.FALSE.toString();
    }
}
