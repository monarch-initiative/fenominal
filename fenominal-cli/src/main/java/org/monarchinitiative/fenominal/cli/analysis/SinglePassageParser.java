package org.monarchinitiative.fenominal.cli.analysis;

import org.monarchinitiative.fenominal.core.FenominalRunTimeException;
import org.monarchinitiative.fenominal.core.TextToHpoMapper;
import org.monarchinitiative.fenominal.core.corenlp.MappedSentencePart;
import org.monarchinitiative.fenominal.core.hpo.HpoLoader;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;
import picocli.CommandLine;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class SinglePassageParser extends PassageParser {


    private final String input;


    public SinglePassageParser(String hpoJsonPath, String input, String output) {
        super(hpoJsonPath, output);
        this.input = input;
    }

    public void parse() {
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
