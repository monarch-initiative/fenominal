package org.monarchinitiative.fenominal.core.textmapper;

import org.monarchinitiative.fenominal.core.corenlp.FmCoreDocument;
import org.monarchinitiative.fenominal.core.corenlp.MappedSentencePart;
import org.monarchinitiative.fenominal.core.corenlp.SimpleSentence;
import org.monarchinitiative.fenominal.core.decorators.DecorationProcessorService;
import org.monarchinitiative.fenominal.core.decorators.TokenDecoratorService;

import org.monarchinitiative.fenominal.core.hpo.HpoMatcher;

import org.monarchinitiative.fenominal.core.hpo.HpoConcept;
import org.monarchinitiative.fenominal.core.hpo.DefaultHpoMatcher;
import org.monarchinitiative.fenominal.core.hpo.HpoConceptHit;

import org.monarchinitiative.fenominal.core.lexical.LexicalResources;
import org.monarchinitiative.phenol.ontology.data.Ontology;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClinicalTextMapper {


    private final static int KMER_SIZE = 3;
    private final static String KMER_DB_FILE = "/home/tudor/tmp/kmer.ser";


    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultHpoMatcher.class);
 
    private final TokenDecoratorService tokenDecoratorService;
    private final DecorationProcessorService decorationProcessorService;

    private Map<Boolean, SentenceMapper> sentenceMappers;

    /**
     * TODO: Add k-mer DB file to the constructor + k-mer size !
     */
    public ClinicalTextMapper(Ontology ontology, LexicalResources lexicalResources) {
        this.hpoMatcher = new DefaultHpoMatcher(ontology, lexicalResources);
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


    private List<MappedSentencePart> mapSentence(SimpleSentence ss) {
        List<SimpleToken> nonStopWords = ss.getTokens().stream()
                .filter(Predicate.not(token -> StopWords.isStop(token.getToken())))
                .collect(Collectors.toList());
        nonStopWords = this.tokenDecoratorService.decorate(nonStopWords);
        // key -- sentence start position, value -- candidate matches.
        Map<Integer, List<MappedSentencePart>> candidates = new HashMap<>();
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
                    MappedSentencePart mappedSentencePart =
                            decorationProcessorService.process(chunk, nonStopWords, opt.get());

//                            new MappedSentencePart(chunk, opt.get().getHpoId());
                    candidates.putIfAbsent(mappedSentencePart.getStartpos(), new ArrayList<>());
                    candidates.get(mappedSentencePart.getStartpos()).add(mappedSentencePart);
                }
            }
        }
        // When we get here, we have zero, one, or more MappedSentenceParts.
        // Our heuristic is to take the longest match first
        // First get and sort the start positions
        List<Integer> startPosList = new ArrayList<>(candidates.keySet());
        Collections.sort(startPosList);
        int currentSpan = -1;
        List<MappedSentencePart> mappedSentencePartList = new ArrayList<>();
        for (int i : startPosList) {
            if (i < currentSpan) {
                continue;
            }
            List<MappedSentencePart> candidatesAtPositionI = candidates.get(i);
            MappedSentencePart longest = getLongestPart(candidatesAtPositionI);
            mappedSentencePartList.add(longest);
            // advance to the last position of the current match
            // note that this is String position convention, and so the next hist could start at
            // currentSpan, but cannot be less than currentSpan without overlapping.
            currentSpan = longest.getEndpos();
        }
        return mappedSentencePartList;
    }

    private MappedSentencePart getLongestPart(List<MappedSentencePart> candidatesAtPositionI) {
        // we should be guaranteed to have at least one list entry -- TODO do we need to check?
        MappedSentencePart max = candidatesAtPositionI.get(0);
        for (int i = 1; i < candidatesAtPositionI.size(); i++) {
            if (candidatesAtPositionI.get(i).getEndpos() > max.getEndpos()) {
                max = candidatesAtPositionI.get(i);
            }
        }
        return max;
    }


    public Ontology getHpo() {
        return this.hpoMatcher.getHpoPhenotypicAbnormality();
    }


}
