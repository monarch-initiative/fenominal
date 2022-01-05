package org.monarchinitiative.fenominal.gui.controller;

import javafx.fxml.FXML;
import org.monarchinitiative.phenofx.questionnnaire.QuestionnairePane;
import org.monarchinitiative.phenofx.questionnnaire.phenoitem.PhenoAnswer;
import org.monarchinitiative.phenofx.questionnnaire.phenoitem.PhenoItem;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Scope("prototype")
public class QuestionnaireController {

    private final Ontology ontology;

    @FXML
    QuestionnairePane quest;

    public QuestionnaireController(Ontology ontology) {
        this.ontology = ontology;
    }

    @FXML
    private void initialize() {

    }

    public void setQuestions(List<PhenoItem> questions) {
        quest.setQuestionnaire(this.ontology, questions);
    }

    public List<PhenoAnswer> getAnswers() {
        return quest.getAnswers();
    }
}
