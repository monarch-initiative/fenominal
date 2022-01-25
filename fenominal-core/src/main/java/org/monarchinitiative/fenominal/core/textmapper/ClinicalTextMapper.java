package org.monarchinitiative.fenominal.core.textmapper;

import org.monarchinitiative.fenominal.core.corenlp.FmCoreDocument;
import org.monarchinitiative.fenominal.core.corenlp.MappedSentencePart;
import org.monarchinitiative.fenominal.core.corenlp.SimpleSentence;
import org.monarchinitiative.fenominal.core.decorators.DecorationProcessorService;
import org.monarchinitiative.fenominal.core.decorators.TokenDecoratorService;
import org.monarchinitiative.fenominal.core.hpo.HpoMatcher;
import org.monarchinitiative.fenominal.core.lexical.LexicalResources;
import org.monarchinitiative.phenol.ontology.data.Ontology;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClinicalTextMapper {

    private final static int KMER_SIZE = 3;
    private final static String KMER_DB_FILE = "/home/tudor/tmp/kmer.ser";

    private final HpoMatcher hpoMatcher;
    private final TokenDecoratorService tokenDecoratorService;
    private final DecorationProcessorService decorationProcessorService;

    private Map<Boolean, SentenceMapper> sentenceMappers;

    /**
     * TODO: Add k-mer DB file to the constructor + k-mer size !
     */
    public ClinicalTextMapper(Ontology ontology, LexicalResources lexicalResources) {
        this.hpoMatcher = new HpoMatcher(ontology, lexicalResources);
        this.tokenDecoratorService = new TokenDecoratorService(lexicalResources);
        this.decorationProcessorService = new DecorationProcessorService();
        FuzzySentenceMapper fuzzySentenceMapper = new FuzzySentenceMapper(KMER_DB_FILE, KMER_SIZE, tokenDecoratorService, decorationProcessorService);
        this.sentenceMappers = Map.of(false, new ExactSentenceMapper(hpoMatcher, tokenDecoratorService, decorationProcessorService),
                true, fuzzySentenceMapper.isValid() ? fuzzySentenceMapper : new ExactSentenceMapper(hpoMatcher, tokenDecoratorService, decorationProcessorService));
    }

    public synchronized List<MappedSentencePart> mapText(String text, boolean fuzzy) {
        FmCoreDocument coreDocument = new FmCoreDocument(text);
        List<SimpleSentence> sentences = coreDocument.getSentences();
        List<MappedSentencePart> mappedParts = new ArrayList<>();
        for (var ss : sentences) {
            List<MappedSentencePart> sentenceParts = sentenceMappers.get(fuzzy).mapSentence(ss);
            mappedParts.addAll(sentenceParts);
        }
        return mappedParts;
    }

    public Ontology getHpo() {
        return this.hpoMatcher.getHpoPhenotypicAbnormality();
    }


}
