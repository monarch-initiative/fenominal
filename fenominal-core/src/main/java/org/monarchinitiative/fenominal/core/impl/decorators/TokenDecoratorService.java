package org.monarchinitiative.fenominal.core.impl.decorators;

import org.monarchinitiative.fenominal.core.impl.corenlp.SimpleToken;
import org.monarchinitiative.fenominal.core.impl.decorators.impl.NegationTokenDecorator;
import org.monarchinitiative.fenominal.core.impl.lexical.LexicalResources;

import java.util.ArrayList;
import java.util.List;

public class TokenDecoratorService {

    private final LexicalResources lexicalResources;

    private final List<TokenDecorator> decorators;

    public TokenDecoratorService(LexicalResources lexicalResources) {
        this.lexicalResources = lexicalResources;
        decorators = new ArrayList<>();
        this.registerDecorators();
    }

    /**
     * TODO: Implement later on using factory pattern and components instantiated via external config options
     */
    private void registerDecorators() {
        decorators.add(new NegationTokenDecorator(lexicalResources.getNegationClues()));
    }

    public List<SimpleToken> decorate(List<SimpleToken> tokens) {
        List<SimpleToken> decoratedTokens = new ArrayList<>();
        for (SimpleToken simpleToken : tokens) {
            for (TokenDecorator tokenDecorator : decorators) {
                decoratedTokens.add(tokenDecorator.decorate(simpleToken));
            }
        }
        return decoratedTokens;
    }
}
