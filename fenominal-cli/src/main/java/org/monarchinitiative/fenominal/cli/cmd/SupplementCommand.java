package org.monarchinitiative.fenominal.cli.cmd;


import org.monarchinitiative.fenominal.core.TextToHpoMapper;
import org.monarchinitiative.fenominal.core.corenlp.MappedSentencePart;
import org.monarchinitiative.fenominal.core.except.FenominalRunTimeException;
import org.monarchinitiative.fenominal.json.JsonHpoParser;
import picocli.CommandLine;

import java.io.*;
import java.util.*;
import java.util.concurrent.Callable;


/**
 * Parse a typical supplemental material that describes multiple affected individuals and
 * count HPO features. This simple prototype will serve to help develop a fuller GUI version.
 * We assume that the individual case reports are separated by a line that starts with ###
 * and the following line has the name of the patient
 * To use this app, copy the supplement to a text file and format accordingly.
 * The default output is supplementDecoded.txt
 */
@CommandLine.Command(name = "supplement", aliases = {"S"},
        mixinStandardHelpOptions = true,
        description = "Parse supplement with multiple affected persons")
public class SupplementCommand implements Callable<Integer> {
    @CommandLine.Option(names = {"--hp"}, description = "path to HP obo file")
    private String hpoJsonPath="data/hp.json";
    @CommandLine.Option(names = {"-i","--input"}, description = "path to input file", required = true)
    private String input;
    @CommandLine.Option(names = {"-0","--output"}, description = "path to output file")
    private String output = "supplementDecoded.txt";



    @Override
    public Integer call() throws Exception {
        Map<String,String> cohortMap = getCohort();
        /*
        Map<TermId, List<String>> termToProbandMap = new HashMap<>();
        TextToHpoMapper mapper = new TextToHpoMapper(hpoJsonPath);
        JsonHpoParser jparser = new JsonHpoParser(hpoJsonPath);
        Ontology hpo = jparser.getHpo();
        for (var e : cohortMap.entrySet()) {
            String individualId = e.getKey();
            String clinicalVignette = e.getValue();
            List<MappedSentencePart> mappedSentenceParts = mapper.mapText(clinicalVignette);
            Set<TermId> hpoIdSet = new HashSet<>();
            for (var msp : mappedSentenceParts) {
                hpoIdSet.add(msp.getTid());
            }
            for (TermId tid : hpoIdSet) {
                termToProbandMap.putIfAbsent(tid, new ArrayList<>());
                termToProbandMap.get(tid).add(individualId);
            }
        }
        int N = cohortMap.size();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(this.output))) {
            for (var e : termToProbandMap.entrySet()) {
                TermId tid = e.getKey();
                String label = hpo.getTermMap().get(tid).getName();
                if (label == null) {
                    System.err.println("[ERROR] Could not find label for " + tid.getValue());
                    continue;
                }
                List<String> probands = e.getValue();
                bw.write(String.format("%s (%s): %d/%d\n", label, tid.getValue(), probands.size(), N));
                bw.write(String.format("\t%s\n", String.join("; ", probands)));
                bw.write("#########\n");

            }
            for (var e : cohortMap.entrySet()) {
                String individualId = e.getKey();
                String clinicalVignette = e.getValue();
                bw.write(String.format("%s\n%s\n\n#########\n", individualId, clinicalVignette));
            }
        }
        */

        return 0;
    }


    private Map<String,String> getCohort() {
        Map<String,String> cohortMap = new HashMap<>();
        File f = new File(input);
        if (! f.isFile()) {
            throw new FenominalRunTimeException("Could not find input file at \"" + input + "\"");
        }
        String currentProband = null;
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line = "";
            // go to first line with text, assume it is the name of the first proband
            while (line.isEmpty()) {
                line = br.readLine();
            }
            currentProband = line.trim();
            while ((line = br.readLine()) != null) {
                if (! line.startsWith("###")) {
                    sb.append(line);
                } else {
                    cohortMap.put(currentProband, sb.toString());
                    sb = new StringBuilder();
                    currentProband = br.readLine().trim(); // start next proband
                }
            }
            // get last proband
            cohortMap.put(currentProband, sb.toString());
        } catch (IOException e) {
            throw new FenominalRunTimeException(e.getMessage());
        }
        return cohortMap;
    }



}
