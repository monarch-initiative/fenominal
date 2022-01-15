package org.monarchinitiative.fenominal.core.decorators.impl;

import org.monarchinitiative.fenominal.core.corenlp.SimpleToken;
import org.monarchinitiative.fenominal.core.decorators.Decorations;
import org.monarchinitiative.fenominal.core.decorators.TokenDecorator;

import java.util.Map;

public record NegationTokenDecorator(
        Map<String, String> negationClues) implements TokenDecorator {

    @Override
    public SimpleToken decorate(SimpleToken simpleToken) {
        if (negationClues.containsKey(simpleToken.getToken().toLowerCase())) {
            simpleToken.addDecoration(Decorations.NEGATION.name());
        }
        return simpleToken;
    }
}
