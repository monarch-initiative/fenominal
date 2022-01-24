package org.monarchinitiative.fenominal.gui.questionnaire;

import org.monarchinitiative.fenominal.gui.questionnaire.io.QuestionParser;
import org.monarchinitiative.fenominal.gui.questionnaire.phenoitem.PhenoItem;
import org.monarchinitiative.phenol.base.PhenolRuntimeException;
import org.monarchinitiative.phenol.ontology.data.Ontology;

import java.io.File;
import java.io.InputStream;
import java.util.List;

public class PhenoQuestionnaire {

    private final List<PhenoItem> questions;

    private PhenoQuestionnaire(List<PhenoItem> phenoQuestions) {
        this.questions = phenoQuestions;
    }

    public List<PhenoItem> getQuestions() {
        return questions;
    }

    /**
     * This method should be used to load custom questionnaires
     * @param customQuestionnaire TSV file with questions
     * @param ontology reference to HPO
     * @return {@link PhenoQuestionnaire} object with custom questions
     */
    public static PhenoQuestionnaire custom(File customQuestionnaire, Ontology ontology) {
        QuestionParser parser = new QuestionParser(customQuestionnaire, ontology);
        List<PhenoItem> ques =  parser.getPhenoItemList();
        return new PhenoQuestionnaire(ques);
    }

    /**
     * This method should be used to load the predefined questionnaire with developmental
     * milestone-related questions
     * @param ontology reference to HPO
     * @return {@link PhenoQuestionnaire} object with development questions
     */
    public static PhenoQuestionnaire development(Ontology ontology) {
        String questionFilePath = "/questions/development.tsv";
        InputStream is = PhenoQuestionnaire.class.getResourceAsStream(questionFilePath);
        if (is == null) {
            throw new PhenolRuntimeException("Could not find development questionnaire file" + questionFilePath);
        } else {
            System.out.println("GOT DEV RES");
        }
        QuestionParser parser = new QuestionParser(is, ontology);
        List<PhenoItem> ques =  parser.getPhenoItemList();
        return new PhenoQuestionnaire(ques);
    }


}
