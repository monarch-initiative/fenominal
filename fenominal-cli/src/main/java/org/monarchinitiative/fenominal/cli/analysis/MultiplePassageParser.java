package org.monarchinitiative.fenominal.cli.analysis;

import org.monarchinitiative.fenominal.core.corenlp.MappedSentencePart;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class MultiplePassageParser extends  PassageParser{


    private final String input;

    private Map<TermId, Integer> countsMap;

    private final int mincount;

    public MultiplePassageParser(String hpoJsonPath, String input, String output, int min) {
        super(hpoJsonPath, output);
        this.input = input;
        countsMap = new HashMap<>();
        this.mincount = min;
    }

    public void parse() {
        Path path = Paths.get(this.input);
        int n_cases = 0;
        try {
            List<String> lines = Files.readAllLines(path);
            int LEN = lines.size();
            int i = 0;
            for (int j = 0; j< LEN; j++) {
                if (lines.get(j).isEmpty()) {
                    // combine lines i to j into text block
                    List<String> block = lines.stream().skip(i).limit(j).collect(Collectors.toList());
                    String text = String.join(" ", block);
                    List<MappedSentencePart> mappedSentenceParts = getMappedSentenceParts(text);
                    for (var msp : mappedSentenceParts) {
                        countsMap.putIfAbsent(msp.getTid(), 0);
                        countsMap.merge(msp.getTid(), 1, Integer::sum);
                    }
                    n_cases++;
                    i = j+i;
                }
            }
            BufferedWriter writer = new BufferedWriter(new FileWriter(this.output));
            for (var e : countsMap.entrySet()) {
                TermId tid = e.getKey();
                Integer count = e.getValue();
                if (mincount > count) {
                    continue;
                }
                Optional<String> opt = ontology.getTermLabel(tid);
                if (opt.isEmpty()) {
                    System.err.println("[ERROR] Could not find label for " + tid.getValue());
                }
                writer.write(tid.getValue() + "\t" + opt.get() + "\t" + count + "/" + n_cases + "\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
