package org.monarchinitiative.fenominal.cli;

import org.monarchinitiative.fenominal.cli.cmd.DownloadCommand;
import org.monarchinitiative.fenominal.cli.cmd.ParseCommand;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(name = "fenominal",
        mixinStandardHelpOptions = true,
        version = "0.5.0",
        description = "phenotype/disease NER")
public class Main implements Callable<Integer> {

    public static void main(String[] args) {
        if (args.length == 0) {
            // if the user doesn't pass any command or option, add -h to show help
            args = new String[]{"-h"};
        }
        CommandLine cline = new CommandLine(new Main())
                .addSubcommand("download", new DownloadCommand())
                .addSubcommand("parse", new ParseCommand());
        cline.setToggleBooleanFlags(false);
        int exitCode = cline.execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() {
        // work done in subcommands
        return 0;
    }

}
