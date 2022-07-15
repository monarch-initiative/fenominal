package org.monarchinitiative.fenominal.core.hpo;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.fenominal.FenominalTermMiner;
import org.monarchinitiative.fenominal.model.MinedTerm;
import org.monarchinitiative.phenol.base.PhenolRuntimeException;
import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.ontology.data.Ontology;
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

    private static FenominalTermMiner miner = null;

    private static String report1entireFileContents = null;

    private static Ontology hpo = null;


    private String decode(MinedTerm smt, String text) {
        String sbs = text.substring(Math.max(0,smt.getBegin() -1), smt.getEnd());
        TermId tid = TermId.of(smt.getTermId());
        Optional<String> opt = hpo.getTermLabel(tid);
        String label = opt.orElse("n/a");
        return String.format("%s [%s] - %s%s", label, smt.getTermId(), sbs, (smt.isPresent()?"":" (excluded)"));
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
        url = classLoader.getResource("hpo/hp.json");
        if (url == null) {
            throw new FileNotFoundException("Could not find hp.json for testing");
        }
        File hpoFile = new File(url.getFile());
        if (! hpoFile.isFile()) {
            throw new FileNotFoundException("Could not get report1.txt from URL");
        }
        hpo = OntologyLoader.loadOntology(hpoFile);
        miner = new FenominalTermMiner(hpo);
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
        Collection<MinedTerm> terms = miner.doMining(sentence);
        for (MinedTerm mt : terms) {
            assertTrue(mt.isPresent());
        }
        assertEquals(3, terms.size());
    }

    @Test
    public void sentence2() {
        String sentence = "She is being seen for reevaluation, having last been seen by myself on\n" +
                "10/10/2016. In the interim, she had botulinum toxin injections on 11/08/2016 under sedation.";
        Collection<MinedTerm> terms = miner.doMining(sentence);
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
        Collection<MinedTerm> terms = miner.doMining(sentence);
        for (MinedTerm mt : terms) {
            assertTrue(mt.isPresent());
        }
        // TermIds are represented as Strings
        List<String> termIdList = terms.stream().map(MinedTerm::getTermId).toList();
        assertEquals("HP:0032443", termIdList.get(0)); // HP:0032443 = Past medical history
        assertEquals("HP:0001622", termIdList.get(1)); // Premature birth HP:0001622
        assertEquals("HP:0100021", termIdList.get(2)); //Cerebral palsy HP:0100021
        assertEquals("HP:0001263", termIdList.get(3)); //Global developmental delay HP:0001263
        assertEquals("HP:0000750", termIdList.get(4)); //Delayed speech and language development HP:0000750
        assertEquals(5,  terms.size());
    }

    /**
     * microcephalic and spilling of saliva need to be added as synonyms.
     * so we expect to pick up one match with the current hp.json used in test/resources
     */
    @Test
    public void sentence4() {
        String sentence = """
                Microcephalic with open-mouth posture, spilling of saliva.
                """;
        Collection<MinedTerm> terms = miner.doMining(sentence);
        for (MinedTerm mt : terms) {
            assertTrue(mt.isPresent());
        }
        assertEquals(1,  terms.size());
    }


    @Test
    public void sentence5() {
        String sentence = """
                Spine is straight with no scoliosis.
                """;
        Collection<MinedTerm> terms = miner.doMining(sentence);
        for (MinedTerm mt : terms) {
            System.out.println(mt.getTermId());
        }
        assertEquals(1,  terms.size());
        MinedTerm mt  = terms.iterator().next();
        assertFalse(mt.isPresent());
        assertEquals("HP:0002650", mt.getTermId());
        //Scoliosis HP:0002650
    }












}
