package org.monarchinitiative.fenominal.cli.analysis;

import org.monarchinitiative.fenominal.core.FenominalRunTimeException;
import org.monarchinitiative.fenominal.core.TextToHpoMapper;
import org.monarchinitiative.fenominal.core.corenlp.MappedSentencePart;
import org.monarchinitiative.fenominal.core.hpo.HpoLoader;
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
import java.util.List;

public class PassageParser {
    Logger LOGGER = LoggerFactory.getLogger(PassageParser.class);
    private final TextToHpoMapper mapper;
    protected final Ontology ontology;

    private final String input;
    protected final String output;


    public PassageParser(String hpoJsonPath,String input, String output) {
        this.input = input;
        this.output = output;
        HpoLoader hpoLoader = new HpoLoader(hpoJsonPath);
        this.ontology = hpoLoader.getHpo();
        this.mapper = new TextToHpoMapper(this.ontology);
    }


    protected List<MappedSentencePart> getMappedSentenceParts (String content) {
        return mapper.mapText(content);
    }


    public void parse() {
        LOGGER.info("Parsing {} and writing results to {}", input, output);
        File f = new File(input);
        if (!f.isFile()) {
            throw new FenominalRunTimeException("Could not find input file at \"" + input + "\"");
        }
        try {
            String content = new String(Files.readAllBytes(Paths.get(input)));
            List<MappedSentencePart> mappedSentenceParts = getMappedSentenceParts(content);
            BufferedWriter writer = new BufferedWriter(new FileWriter(this.output));
            for (var mp : mappedSentenceParts) {
                TermId tid = mp.getTid();
                var opt = ontology.getTermLabel(tid);
                if (opt.isEmpty()) {
                    // should never happen
                    System.err.println("[ERROR] Could not find label for " + tid.getValue());
                    continue;
                }
                String label = opt.get();
                writer.write(tid.getValue() + "\t" + label + "\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
