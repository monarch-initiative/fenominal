package org.monarchinitiative.fenominal.gui.questionnaire.io;

import org.monarchinitiative.fenominal.gui.questionnaire.phenoitem.*;
import org.monarchinitiative.phenol.base.PhenolRuntimeException;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class QuestionParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(QuestionParser.class);


    private final List<PhenoItem> phenoItemList;

    private final static List<String> headerFields = List.of("question.type",
            "hp.id", "hp.label", "threshold.year", "threshold.month",
            "threshold.day", "older.younger", "question.text");
    private final static String header = String.join("\t", headerFields);
    private final static int N_HEADER_FIELDS = headerFields.size();


    public QuestionParser(InputStream is, Ontology ontology) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            this.phenoItemList = ingest(br, ontology);
        } catch (IOException e) {
            LOGGER.error("Could not read question file: {}", e.getMessage());
            throw new PhenolRuntimeException("Could not initialize phenoitems from stream");
        }
    }


    public QuestionParser(File questionFile, Ontology ontology) {
        try (BufferedReader br = new BufferedReader(new FileReader(questionFile))) {
           this.phenoItemList = ingest(br, ontology);
        } catch (IOException e) {
            LOGGER.error("Could not read question file: {}", e.getMessage());
            throw new PhenolRuntimeException("Could not initialize phenoitems from file");
        }
    }

    private List<PhenoItem> ingest(BufferedReader br, Ontology ontology) throws IOException {
        List<PhenoItem> items = new ArrayList<>();
        String line = br.readLine();
        if (! line.equals(header)) {
            throw new PhenolRuntimeException("Malformed header: \""+line+"\"");
        }
        while ((line = br.readLine()) != null) {
            String[] fields = line.split("\t");
            String label = fields[1].trim();
            TermId tid = TermId.of(fields[2].trim());
            if (!ontology.containsTerm(tid)) {
                LOGGER.error("Could not find term for {}", line);
                continue;
            }
            Term term = ontology.getTermMap().get(tid);
            switch (fields[0].toLowerCase(Locale.ROOT)) {
                case "simple" -> {
                    if (fields.length != 3) {
                        throw new PhenolRuntimeException("Malformed simple line with "
                                + fields.length + " fields. " + line);
                    }
                    PhenoItem item = new SimplePhenoItem(term);
                    items.add(item);
                }
                case "age.threshold" -> {
                    if (fields.length != N_HEADER_FIELDS) {
                        throw new PhenolRuntimeException("Malformed age.threshold line with "
                                + fields.length + " fields. " + line);
                    }
                    // The following fields may be left empty to signify n/a, we assign 0 in this case
                    int y = fields[3].length() > 0 ? Integer.parseInt(fields[3]) : 0;
                    int m = fields[4].length() > 0 ? Integer.parseInt(fields[4]) : 0;
                    int d = fields[5].length() > 0 ? Integer.parseInt(fields[5]) : 0;
                    PhenoAge phenoAge = new PhenoAge(y, m, d);
                    String rule = fields[6].trim();
                    AgeRule ageRule;
                    if (rule.equals("older")) {
                        ageRule = AgeRule.olderThanIsAbnormal(phenoAge);
                    } else if (rule.equals("younger")) {
                        ageRule = AgeRule.youngerThanIsAbnormal(phenoAge);
                    } else {
                        throw new PhenolRuntimeException("Could not recognize age.rule " + line);
                    }
                    String question = fields[7].trim();
                    PhenoItem item =
                            new AgeThresholdPhenoItem(term, ageRule, question);
                    items.add(item);
                }
            }
        }
        return List.copyOf(items);
    }

    public List<PhenoItem> getPhenoItemList() {
        return phenoItemList;
    }

}
