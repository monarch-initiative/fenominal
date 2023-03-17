package org.monarchinitiative.fenominal.core.impl.textmapper;

import org.monarchinitiative.fenominal.core.impl.corenlp.SimpleSentence;
import org.monarchinitiative.fenominal.core.impl.corenlp.SimpleToken;
import org.monarchinitiative.fenominal.core.impl.corenlp.StopWords;
import org.monarchinitiative.fenominal.core.impl.decorators.DecorationProcessorService;
import org.monarchinitiative.fenominal.core.impl.decorators.TokenDecoratorService;
import org.monarchinitiative.fenominal.core.impl.hpo.HpoMatcher;
import org.monarchinitiative.fenominal.model.impl.DetailedMinedTerm;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ExactSentenceMapper extends AbstractSentenceMapper implements SentenceMapper {

    public ExactSentenceMapper(HpoMatcher hpoMatcher, TokenDecoratorService tokenDecoratorService, DecorationProcessorService decorationProcessorService) {
        super(hpoMatcher, tokenDecoratorService, decorationProcessorService);
    }

    public List<DetailedMinedTerm> mapSentence(SimpleSentence ss) {
        List<SimpleToken> nonStopWords = ss.getTokens().stream()
                .filter(Predicate.not(token -> StopWords.isStop(token.getToken())))
                .collect(Collectors.toList());
        nonStopWords = super.tokenDecoratorService.decorate(nonStopWords);
        return super.mapSentence(nonStopWords);
    }

}
