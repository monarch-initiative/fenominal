package org.monarchinitiative.fenominal.core.impl.textmapper;

import org.monarchinitiative.fenominal.core.impl.corenlp.FmCoreDocument;
import org.monarchinitiative.fenominal.core.impl.corenlp.SimpleSentence;
import org.monarchinitiative.fenominal.core.impl.corenlp.SimpleToken;
import org.monarchinitiative.fenominal.core.impl.corenlp.StopWords;
import org.monarchinitiative.fenominal.core.impl.decorators.DecorationProcessorService;
import org.monarchinitiative.fenominal.core.impl.decorators.TokenDecoratorService;
import org.monarchinitiative.fenominal.core.impl.hpo.DefaultHpoMatcher;
import org.monarchinitiative.fenominal.core.impl.hpo.HpoConceptHit;
import org.monarchinitiative.fenominal.core.impl.hpo.HpoMatcher;
import org.monarchinitiative.fenominal.core.impl.kmer.KmerDB;
import org.monarchinitiative.fenominal.core.impl.kmer.KmerGenerator;
import org.monarchinitiative.fenominal.core.impl.lexical.LexicalResources;
import org.monarchinitiative.fenominal.model.MinedSentence;
import org.monarchinitiative.fenominal.model.MinedTermWithMetadata;
import org.monarchinitiative.fenominal.model.impl.DefaultMinedSentence;
import org.monarchinitiative.fenominal.model.impl.DetailedMinedTerm;
import org.monarchinitiative.phenol.base.PhenolRuntimeException;
import org.monarchinitiative.phenol.ontology.data.MinimalOntology;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ClinicalTextMapper {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClinicalTextMapper.class);
    private final TokenDecoratorService tokenDecoratorService;
    private final DecorationProcessorService decorationProcessorService;
    private final HpoMatcher hpoMatcher;
    private final Map<Boolean, SentenceMapper> sentenceMappers;

    public ClinicalTextMapper(MinimalOntology ontology, LexicalResources lexicalResources) {
        LOGGER.trace("Initializing ClinicalTextMapper without kmer file (kmer resources will be generated on the fly)");
        this.hpoMatcher = new DefaultHpoMatcher(ontology, lexicalResources);
        this.tokenDecoratorService = new TokenDecoratorService(lexicalResources);
        this.decorationProcessorService = new DecorationProcessorService();
        KmerGenerator kmerGenerator = new KmerGenerator(ontology);
        Optional<KmerDB> opt = kmerGenerator.getKmerDB();
        if (opt.isEmpty()) {
            throw new PhenolRuntimeException("Could not initialze KMer DB on the fly");
        }
        KmerDB kmerDB = opt.get();
        TBlatSentenceMapper tBlatSentenceMapper = new TBlatSentenceMapper(kmerDB, lexicalResources, hpoMatcher, tokenDecoratorService, decorationProcessorService);
        this.sentenceMappers = Map.of(false, new ExactSentenceMapper(hpoMatcher, tokenDecoratorService, decorationProcessorService),
                true, tBlatSentenceMapper);
    }

    public synchronized List<DetailedMinedTerm> mapText(String text, boolean fuzzy) {
        FmCoreDocument coreDocument = new FmCoreDocument(text);
        List<SimpleSentence> sentences = coreDocument.getSentences();
        List<DetailedMinedTerm> mappedParts = new ArrayList<>();
        for (var ss : sentences) {
            List<DetailedMinedTerm> sentenceParts = sentenceMappers.get(fuzzy).mapSentence(ss);
            mappedParts.addAll(sentenceParts);
        }
        return mappedParts;
    }


    public synchronized List<MinedSentence> mapSentences(String text, boolean fuzzy) {
        FmCoreDocument coreDocument = new FmCoreDocument(text);
        List<SimpleSentence> sentences = coreDocument.getSentences();
        List<MinedSentence> minedSentenceList = new ArrayList<>();
        for (var ss : sentences) {
            List<DetailedMinedTerm> sentenceParts = sentenceMappers.get(fuzzy).mapSentence(ss);
            MinedSentence ms = new DefaultMinedSentence(sentenceParts, ss.getSentence());
            minedSentenceList.add(ms);
        }
        return minedSentenceList;
    }


    private List<MinedTermWithMetadata> mapSentence(SimpleSentence ss) {
        List<SimpleToken> nonStopWords = ss.getTokens().stream()
                .filter(Predicate.not(token -> StopWords.isStop(token.getToken())))
                .collect(Collectors.toList());
        nonStopWords = this.tokenDecoratorService.decorate(nonStopWords);
        // key -- sentence start position, value -- candidate matches.
        Map<Integer, List<MinedTermWithMetadata>> candidates = new HashMap<>();
        int LEN = Math.min(10, nonStopWords.size()); // check for maximum of 10 words TODO -- what is best option here?
        for (int i = 1; i <= LEN; i++) {
            Partition<SimpleToken> partition = new Partition<>(nonStopWords, i);
            for (List<SimpleToken> chunk : partition) {
                if (chunk.size() < i) {
                    continue; // last portion is smaller, we will get it in corresponding loop
                }
                List<String> stringchunk = chunk.stream().map(SimpleToken::getToken).collect(Collectors.toList());
                Optional<HpoConceptHit> opt = this.hpoMatcher.getMatch(stringchunk);
                if (opt.isPresent()) {
                    double TODO_DEFAULT_SIM = 1.0;
                    TermId hpoId = opt.get().hpoConcept().getHpoId();
                    MinedTermWithMetadata mappedSentencePart =
                            decorationProcessorService.process(chunk, nonStopWords, hpoId, TODO_DEFAULT_SIM);

//                            new MappedSentencePart(chunk, opt.get().getHpoId());
                    candidates.putIfAbsent(mappedSentencePart.getBegin(), new ArrayList<>());
                    candidates.get(mappedSentencePart.getBegin()).add(mappedSentencePart);
                }
            }
        }
        // When we get here, we have zero, one, or more MappedSentenceParts.
        // Our heuristic is to take the longest match first
        // First get and sort the start positions
        List<Integer> startPosList = new ArrayList<>(candidates.keySet());
        Collections.sort(startPosList);
        int currentSpan = -1;
        List<MinedTermWithMetadata> mappedSentencePartList = new ArrayList<>();
        for (int i : startPosList) {
            if (i < currentSpan) {
                continue;
            }
            List<MinedTermWithMetadata> candidatesAtPositionI = candidates.get(i);
            MinedTermWithMetadata longest = getLongestPart(candidatesAtPositionI);
            mappedSentencePartList.add(longest);
            // advance to the last position of the current match
            // note that this is String position convention, and so the next hist could start at
            // currentSpan, but cannot be less than currentSpan without overlapping.
            currentSpan = longest.getEnd();
        }
        return mappedSentencePartList;
    }

    private MinedTermWithMetadata getLongestPart(List<MinedTermWithMetadata> candidatesAtPositionI) {
        // we should be guaranteed to have at least one list entry -- TODO do we need to check?
        MinedTermWithMetadata max = candidatesAtPositionI.get(0);
        for (int i = 1; i < candidatesAtPositionI.size(); i++) {
            if (candidatesAtPositionI.get(i).getEnd() > max.getEnd()) {
                max = candidatesAtPositionI.get(i);
            }
        }
        return max;
    }

}
