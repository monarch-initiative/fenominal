package org.monarchinitiative.fenominal.core.impl.decorators.impl;

import org.monarchinitiative.fenominal.core.impl.corenlp.SimpleToken;
import org.monarchinitiative.fenominal.core.impl.decorators.DecorationProcessor;
import org.monarchinitiative.fenominal.core.impl.decorators.Decoration;

import java.util.List;

public class NegationDecorationProcessor implements DecorationProcessor {

    public NegationDecorationProcessor() {

    }

    @Override
    public Decoration getDecoration() {
        return Decoration.NEGATION;
    }

    @Override
    public String getProcessedValue(List<SimpleToken> chunk, List<SimpleToken> nonStopWords) {
        if (chunk.isEmpty()) {
            return Boolean.FALSE.toString();
        }

        int negPosition = Integer.MAX_VALUE;
        for (SimpleToken simpleToken : nonStopWords) {
            if (simpleToken.hasDecoration(Decoration.NEGATION.name())) {
                negPosition = simpleToken.getStartpos();
                break;
            }
        }

        return chunk.get(0).getStartpos() > negPosition ?  Boolean.TRUE.toString() : Boolean.FALSE.toString();
    }
}
