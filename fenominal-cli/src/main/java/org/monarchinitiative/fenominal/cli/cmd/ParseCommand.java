package org.monarchinitiative.fenominal.cli.cmd;

import org.monarchinitiative.fenominal.cli.analysis.PassageParser;
import picocli.CommandLine;


import java.io.File;
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

    @CommandLine.Option(names = {"-k", "--kmer"}, required = false, description = "path to kmer file")
    private String kmerFile = "data/kmer5.ser";

    @Override
    public Integer call() {
        File f = new File(hpoJsonPath);
        if (! f.isFile()) {
            System.out.printf("[ERROR] Could not find hp.json file at %s\nRun download command first\n", hpoJsonPath);
        }
        PassageParser parser = new PassageParser(hpoJsonPath, input, output);
        parser.parse();
        return 0;
    }
}
