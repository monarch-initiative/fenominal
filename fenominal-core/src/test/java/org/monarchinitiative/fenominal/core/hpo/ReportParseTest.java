package org.monarchinitiative.fenominal.core.hpo;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.fenominal.core.TestResources;
import org.monarchinitiative.fenominal.core.impl.NonFuzzyTermMiner;
import org.monarchinitiative.fenominal.model.MinedSentence;
import org.monarchinitiative.fenominal.model.MinedTerm;
import org.monarchinitiative.fenominal.model.MinedTermWithMetadata;
import org.monarchinitiative.phenol.base.PhenolRuntimeException;
import org.monarchinitiative.phenol.ontology.data.MinimalOntology;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ReportParseTest {

    private static NonFuzzyTermMiner miner = null;

    private static String report1entireFileContents = null;

    private static final MinimalOntology hpo = TestResources.hpo();


    private String decode(MinedTerm smt, String text) {
        String sbs = text.substring(Math.max(0,smt.getBegin() -1), smt.getEnd());
        TermId tid = TermId.of(smt.getTermIdAsString());
        String label = hpo.termForTermId(tid)
                .map(Term::getName)
                .orElse("n/a");
        return String.format("%s [%s] - %s%s", label, smt.getTermIdAsString(), sbs, (smt.isPresent()?"":" (excluded)"));
    }

    @BeforeAll
    public static void init() throws IOException {
        ClassLoader classLoader = ReportParseTest.class.getClassLoader();
        if (classLoader == null) {
            throw new PhenolRuntimeException("Could not find class loader");
        }
        URL url = classLoader.getResource("examples/report1.txt");
        if (url == null) {
            throw new FileNotFoundException("Could not find report1.txt");
        }
        File report1 = new File(url.getFile());
        if (! report1.isFile()) {
            throw new FileNotFoundException("Could not get report1.txt from URL");
        }

        miner = new NonFuzzyTermMiner(hpo);
        Path fileName =report1.toPath();

        report1entireFileContents = Files.readString(fileName);
    }

    @Test
    public void testStaticInitSucceeded() {
        assertNotNull(miner);
    }

    /**
     * Expect
     * Past medical history [HP:0032443] -  past medical history
     * Premature birth [HP:0001622] -  premature birth
     * Cerebral palsy [HP:0100021] -  cerebral palsy
     */
    @Test
    public void sentence1() {
        String sentence = """
                XXX is a 5-year-old girl with a past medical history significant for an extreme premature birth
                and resultant spastic diplegic cerebral palsy, function level GMFCS (gross motor function
                classification system) III.""";
        Collection<MinedTerm> terms = miner.mineTerms(sentence);
        for (MinedTerm mt : terms) {
            assertTrue(mt.isPresent());
        }
        assertEquals(2, terms.size());
    }

    @Test
    public void sentence2() {
        String sentence = "She is being seen for reevaluation, having last been seen by myself on\n" +
                "10/10/2016. In the interim, she had botulinum toxin injections on 11/08/2016 under sedation.";
        Collection<MinedTerm> terms = miner.mineTerms(sentence);
        assertEquals(0, terms.size());
    }
    @Test
    public void sentence3() {
        String sentence = """
                PAST MEDICAL HISTORY:
                            1. Premature birth.
                            2. Cerebral palsy.
                            3. Developmental delay.
                            4. Speech delay.
                """;
        Collection<MinedTerm> terms = miner.mineTerms(sentence);
        for (MinedTerm mt : terms) {
            assertTrue(mt.isPresent());
        }
        // TermIds are represented as Strings
        List<String> termIdList = terms.stream().map(MinedTerm::getTermIdAsString).toList();
        assertEquals(4,  terms.size());
        // We do not expect to parse HP:0032443 = Past medical history because we are
        // restricting parsing to descendents of Phenotypic abnormality
        assertEquals("HP:0001622", termIdList.get(0)); // Premature birth HP:0001622
        assertEquals("HP:0100021", termIdList.get(1)); //Cerebral palsy HP:0100021
        assertEquals("HP:0001263", termIdList.get(2)); //Global developmental delay HP:0001263
        assertEquals("HP:0000750", termIdList.get(3)); //Delayed speech and language development HP:0000750

    }

    /**
     * microcephalic and spilling of saliva need to be added as synonyms.
     * so we expect to pick up one match with the current hp.json used in test/resources
     */
    @Test
    public void expectObservedOpenMouth() {
        String sentence = """
                Microcephalic with open-mouth posture, spilling of saliva.
                """;
        Collection<MinedTerm> terms = miner.mineTerms(sentence);
        assertEquals(1,  terms.size());
        MinedTerm mt = terms.iterator().next();
        assertTrue(mt.isPresent());
        assertEquals("HP:0000194", mt.getTermIdAsString());
        //Open mouth HP:0000194
    }


    @Test
    public void expectExcludedScoliosisMinedTerm() {
        String sentence = """
                Spine is straight with no scoliosis.
                """;
        Collection<MinedTerm> terms = miner.mineTerms(sentence);
        assertEquals(1,  terms.size());
        MinedTerm mt  = terms.iterator().next();
        assertFalse(mt.isPresent());
        assertEquals("HP:0002650", mt.getTermIdAsString());
        //Scoliosis HP:0002650
    }


    /**
     * We expect NOT Fever HP:0001945
     */
    @Test
    public void expectExcludedFeverMinedTerm() {
        String sentence = "No symptoms, no fevers";
        Collection<MinedTerm> terms = miner.mineTerms(sentence);
        assertEquals(1,  terms.size());
        MinedTerm mt  = terms.iterator().next();
        assertFalse(mt.isPresent());
        assertEquals("HP:0001945", mt.getTermIdAsString());
        //Fever HP:0001945
    }


    @Test
    public void expectExcludedFeverMinedTermWithMetadata() {
        String sentence = "No symptoms, no fevers";
        Collection<MinedTermWithMetadata> terms = miner.mineTermsWithMetadata(sentence);
        assertEquals(1,  terms.size());
        MinedTerm mt  = terms.iterator().next();
        assertFalse(mt.isPresent());
        assertEquals("HP:0001945", mt.getTermIdAsString());
        //Fever HP:0001945
    }

    @Test
    public void expectExcludedFeverSentence() {
        String sentence = "No symptoms, no fevers";
        Collection<MinedSentence> minedSentences = miner.mineSentences(sentence);
        assertEquals(1,  minedSentences.size());
        MinedSentence minedSentence  = minedSentences.iterator().next();
        Collection<? extends MinedTermWithMetadata> minedTerms = minedSentence.getMinedTerms();
        assertEquals(1,  minedTerms.size());
        MinedTermWithMetadata mt = minedTerms.iterator().next();
        assertFalse(mt.isPresent());
        assertEquals("HP:0001945", mt.getTermIdAsString());
        //Fever HP:0001945
    }






    @Test
    public void expectedExcludedSkinRashMinedTermWithMetadata() {
        String sentence = "Skin: no rash or neurocutaneous stigmata on exposed skin.";
        Collection<MinedTermWithMetadata> terms = miner.mineTermsWithMetadata(sentence);
        assertEquals(1,  terms.size());
        MinedTerm mt  = terms.iterator().next();
        assertFalse(mt.isPresent());
        assertEquals("HP:0000988", mt.getTermIdAsString());
        //Skin rash HP:0000988
    }

    /**
     * Myocardial infarction HP:0001658
     * A synonym is MI and this was being falsely called from the word "Missed" in some contexts
     */
    @Test
    public void doNotFalselyInferMyocardialInfarction() {
        String sentence = "PT Missed Minutes";
        Collection<MinedTermWithMetadata> terms = miner.mineTermsWithMetadata(sentence);
        assertTrue(terms.isEmpty());
    }

    @Test
    public void weaknessShouldNotBeInferredAsAsthenia() {
        String sentence = "reports of R side weakness per mother report";
        Collection<MinedTermWithMetadata> terms = miner.mineTermsWithMetadata(sentence);
        for (var m : terms) {
            System.out.println(m.getTermId());
            System.out.println(m.getMatchingString());
        }
        assertTrue(terms.isEmpty());
    }


    @Test
    public void onlySpasticityNotAstheniaShouldBeInferred(){
        String sentence = "He is nonambulatory and has weakness and spasticity throughout";
        Collection<MinedTermWithMetadata> terms = miner.mineTermsWithMetadata(sentence);
        assertEquals(1, terms.size());
        MinedTermWithMetadata mt = terms.iterator().next();
        //Spasticity HP:0001257
        assertEquals("HP:0001257", mt.getTermIdAsString());
        assertEquals("spasticity", mt.getMatchingString());
    }
}
