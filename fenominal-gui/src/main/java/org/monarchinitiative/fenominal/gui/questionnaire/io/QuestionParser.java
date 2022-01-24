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

public class QuestionParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(QuestionParser.class);


    private final List<PhenoItem> phenoItemList;

    private final static List<String> headerFields = List.of("hp.id", "hp.label",
            "question.text", "detailed.explanation");
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
        if (!line.equals(header)) {
            throw new PhenolRuntimeException("Malformed header: \"" + line + "\"");
        }
        while ((line = br.readLine()) != null) {
            String[] fields = line.split("\t");
            String label = fields[0].trim();
            TermId tid = TermId.of(fields[1].trim());
            if (!ontology.containsTerm(tid)) {
                LOGGER.error("Could not find term for {}", line);
                continue;
            }
            Term term = ontology.getTermMap().get(tid);

            if (fields.length != N_HEADER_FIELDS) {
                throw new PhenolRuntimeException("Malformed age.threshold line with "
                        + fields.length + " fields. " + line);
            }
            String question = fields[2].trim();
            String explanation = fields[3].trim();
            PhenoItem item = new PhenoItemWithQandE(term, question, explanation);
            items.add(item);
        }
        return List.copyOf(items);
    }

    public List<PhenoItem> getPhenoItemList() {
        return phenoItemList;
    }

}
