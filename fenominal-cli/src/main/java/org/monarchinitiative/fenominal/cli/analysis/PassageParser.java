package org.monarchinitiative.fenominal.cli.analysis;

import org.monarchinitiative.fenominal.core.FenominalRunTimeException;
import org.monarchinitiative.fenominal.core.TermMiner;
import org.monarchinitiative.fenominal.model.MinedSentence;
import org.monarchinitiative.fenominal.model.MinedTermWithMetadata;
import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;

public class PassageParser {
    Logger LOGGER = LoggerFactory.getLogger(PassageParser.class);
    private final TermMiner miner;
    protected final Ontology ontology;

    private final String input;
    protected final String output;


    public PassageParser(String hpoJsonPath, String input, String output, boolean exact) {
        this.input = input;
        this.output = output;
        this.ontology = OntologyLoader.loadOntology(new File(hpoJsonPath));
        if (exact) {
            this.miner = TermMiner.defaultNonFuzzyMapper(this.ontology);
        } else {
            this.miner = TermMiner.defaultFuzzyMapper(this.ontology);
        }
    }

    private Collection<MinedSentence> getMappedSentences (String content) {
        return miner.mineSentences(content);
    }


    public void parse(boolean verbose) {
        LOGGER.info("Parsing {} and writing results to {}", input, output);
        File f = new File(input);
        if (!f.isFile()) {
            throw new FenominalRunTimeException("Could not find input file at \"" + input + "\"");
        }
        try {
            String content = new String(Files.readAllBytes(Paths.get(input)));
            Collection<MinedSentence> mappedSentences = getMappedSentences(content);
            BufferedWriter writer = new BufferedWriter(new FileWriter(this.output));
            for (var mp : mappedSentences) {
                String sentence = mp.getText();
                Collection<? extends MinedTermWithMetadata> minedTerms = mp.getMinedTerms();
                for (var mt : minedTerms) {
                    TermId tid = mt.getTermId();
                    var opt = ontology.getTermLabel(tid);
                    if (opt.isEmpty()) {
                        // should never happen
                        System.err.println("[ERROR] Could not find label for " + tid.getValue());
                        continue;
                    }
                    String label = opt.get();
                    String matching = mt.getMatchingString();
                    int start = mt.getBegin();
                    int end = mt.getEnd();
                    String observed = mt.isPresent() ? "observed" : "excluded";
                    String [] fields = {label, tid.getValue(), matching, observed, String.valueOf(start),
                        String.valueOf(end), sentence};
                    writer.write(String.join("\t", fields) +  "\n");
                    if (verbose) {
                        System.out.println(String.join("\t", fields));
                    }
                }
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
