package org.monarchinitiative.fenominal.cli.cmd;

import org.monarchinitiative.fenominal.cli.analysis.PassageParser;
import picocli.CommandLine;


import java.io.File;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "parse", aliases = {"P"},
        mixinStandardHelpOptions = true,
        description = "Parse text")
public class ParseCommand implements Callable<Integer> {
    @CommandLine.Option(names = {"--hp"}, description = "Path to HP json file")
    private String hpoJsonPath="data/hp.json";
    @CommandLine.Option(names = {"-i","--input"}, description = "Path to input file", required = true)
    public String input;
    @CommandLine.Option(names = { "-o", "--output"}, description = "Path to output file (default: ${DEFAULT-VALUE})")
    private String output = "fenominal-mined.txt";

    @CommandLine.Option(names = {"-e", "--exact"}, description = "Use exact matching algorithm")
    private boolean useExactMatching = false;




    @CommandLine.Option(names = {"--verbose"}, description = "show results in shell (default is to just write to file)")
    private boolean verbose;


    @Override
    public Integer call() {
        File f = new File(hpoJsonPath);
        if (! f.isFile()) {
            System.out.printf("[ERROR] Could not find hp.json file at %s\nRun download command first\n", hpoJsonPath);
        }
        PassageParser parser = new PassageParser(hpoJsonPath, input, output, useExactMatching);
        parser.parse(verbose);
        return 0;
    }
}
