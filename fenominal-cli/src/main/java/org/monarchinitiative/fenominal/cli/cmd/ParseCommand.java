package org.monarchinitiative.fenominal.cli.cmd;

import org.monarchinitiative.fenominal.core.TextToHpoMapper;
import org.monarchinitiative.fenominal.core.corenlp.MappedSentencePart;
import org.monarchinitiative.fenominal.core.except.FenominalRunTimeException;
import picocli.CommandLine;


import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "parse", aliases = {"P"},
        mixinStandardHelpOptions = true,
        description = "Parse text")
public class ParseCommand implements Callable<Integer> {

    @CommandLine.Option(names = {"--hp"}, description = "path to HP obo file")
    public String hpoOboPath="data/hp.obo";
    @CommandLine.Option(names = {"-i","--input"}, description = "path to input file", required = true)
    public String input;

    @Override
    public Integer call() throws Exception {
        File f = new File(input);
        if (! f.isFile()) {
            throw new FenominalRunTimeException("Could not find input file at \"" + input + "\"");
        }
        String content = new String ( Files.readAllBytes( Paths.get(input) ) );
        TextToHpoMapper mapper = new TextToHpoMapper(hpoOboPath);
        List<MappedSentencePart> mappedSentenceParts = mapper.mapText(content);
        for (var mp : mappedSentenceParts) {
            System.out.println(mp);
        }
        return 0;
    }
}
