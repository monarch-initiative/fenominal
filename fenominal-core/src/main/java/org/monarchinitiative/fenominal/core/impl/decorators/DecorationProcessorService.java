package org.monarchinitiative.fenominal.core.impl.decorators;

import org.monarchinitiative.fenominal.core.impl.corenlp.SimpleToken;
import org.monarchinitiative.fenominal.core.impl.decorators.impl.NegationDecorationProcessor;
import org.monarchinitiative.fenominal.model.impl.DetailedMinedTerm;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DecorationProcessorService {

    private final List<DecorationProcessor> decorationProcessors;

    public DecorationProcessorService() {
        decorationProcessors = new ArrayList<>();
        this.registerDecorationProcessors();
    }

    /**
     * TODO: Implement later on using factory pattern and components instantiated via external config options
     */
    private void registerDecorationProcessors() {
        decorationProcessors.add(new NegationDecorationProcessor());
    }

    /**
     * TODO: Implement this in a smarter way
     */
    public DetailedMinedTerm process(List<SimpleToken> chunk, List<SimpleToken> nonStopWords, TermId hpoId, double similarity) {

// from develop    public MappedSentencePart process(List<SimpleToken> chunk, List<SimpleToken> nonStopWords, HpoConceptHit conceptHit) {

        Map<String, String> decorations = new LinkedHashMap<>();
        for (DecorationProcessor decorationProcessor : decorationProcessors) {
            decorations.put(decorationProcessor.getDecoration(), decorationProcessor.getProcessedValue(chunk, nonStopWords));
        }
        return new DetailedMinedTerm(chunk, hpoId, similarity, decorations);
    }
}
