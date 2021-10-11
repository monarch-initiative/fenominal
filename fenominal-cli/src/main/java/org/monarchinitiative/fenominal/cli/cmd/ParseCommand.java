package org.monarchinitiative.fenominal.cli.cmd;

import org.monarchinitiative.fenominal.cli.analysis.MultiplePassageParser;
import org.monarchinitiative.fenominal.cli.analysis.PassageParser;
import org.monarchinitiative.fenominal.cli.analysis.SinglePassageParser;
import org.monarchinitiative.fenominal.core.TextToHpoMapper;
import org.monarchinitiative.fenominal.core.corenlp.MappedSentencePart;
import org.monarchinitiative.fenominal.core.FenominalRunTimeException;
import org.monarchinitiative.fenominal.core.hpo.HpoLoader;
import org.monarchinitiative.fenominal.json.OntologyLoader;
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
import java.util.concurrent.Callable;

@CommandLine.Command(name = "parse", aliases = {"P"},
        mixinStandardHelpOptions = true,
        description = "Parse text")
public class ParseCommand implements Callable<Integer> {
    @CommandLine.Option(names = {"--hp"}, description = "path to HP json file")
    private String hpoJsonPath="data/hp.json";
    @CommandLine.Option(names = {"-i","--input"}, description = "path to input file", required = true)
    public String input;
    @CommandLine.Option(names = { "-o", "--output"}, description = "path to output file")
    private String output = "fenominal-mined.txt";
    @CommandLine.Option(names = { "--multiple"}, description = "if true, parse text with multiple passages")
    private boolean multiple = false;
    @CommandLine.Option(names = {"-mincount"}, description = "minimum count for output (multiple)")
    private int mincount = 2;

    @Override
    public Integer call() throws Exception {
        PassageParser parser;
        if (multiple) {
            parser = new MultiplePassageParser(hpoJsonPath, input, output, mincount);
        } else {
            parser = new SinglePassageParser(hpoJsonPath, input, output);
        }
        parser.parse();
        return 0;
    }
}
