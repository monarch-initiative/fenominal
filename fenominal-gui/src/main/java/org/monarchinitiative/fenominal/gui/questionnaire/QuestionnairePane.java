package org.monarchinitiative.fenominal.gui.questionnaire;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.monarchinitiative.fenominal.gui.questionnaire.phenoitem.PhenoAnswer;
import org.monarchinitiative.fenominal.gui.questionnaire.phenoitem.PhenoItem;
import org.monarchinitiative.fenominal.gui.questionnaire.qtable.PhenoqTable;
import org.monarchinitiative.fenominal.gui.questionnaire.qtable.Qphenorow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class QuestionnairePane extends BorderPane {
Logger LOGGER = LoggerFactory.getLogger(QuestionnairePane.class);
    private PhenoqTable phenoqTable;
    private final VBox root = new VBox();
    Button cancelButton = new Button("Cancel");
    Button acceptButton = new Button("Done");

    public QuestionnairePane() {
        super();
        URL cssResource = QuestionnairePane.class.getResource("/QuestionnairePane.css");
        if (cssResource != null) {
            getStylesheets().add(cssResource.toExternalForm());
            getStyleClass().add("pane");
        } else {
            LOGGER.error("Could not load CSS for QuestionnairePane");
        }
        phenoqTable = new PhenoqTable(List.of()); // initialize here with empty list
        HBox buttonBox = new HBox();
        buttonBox.setMinWidth(1000);
        buttonBox.setMaxHeight(20);


        buttonBox.getChildren().add(cancelButton);
        buttonBox.getChildren().add(acceptButton);
        root.getChildren().add(buttonBox);
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Label titleLabel = new Label("HPO-based Questionnaire");
        setTop(titleLabel);

    }

    public void setQuestionnaire(List<PhenoItem> phenoQuestions) {
        List<Qphenorow> phenoRows = phenoQuestions.stream().map(Qphenorow::new).collect(Collectors.toList());
        this.phenoqTable = new PhenoqTable(phenoRows);
        root.getChildren().add(this.phenoqTable);
        HBox buttonBox = new HBox();
        buttonBox.setMinWidth(1000);
        buttonBox.setMaxHeight(20);
        Button cancelButton = new Button("Cancel");
        Button acceptButton = new Button("Done");
        cancelButton.setOnAction((e) -> {
            phenoRows.forEach(Qphenorow::reset);
            Stage stage = (Stage) cancelButton.getScene().getWindow();
            stage.close();
        });
        acceptButton.setOnAction((e) -> {
            Stage stage = (Stage) cancelButton.getScene().getWindow();
            stage.close();
        });
        buttonBox.getChildren().add(cancelButton);
        buttonBox.getChildren().add(acceptButton);
        root.getChildren().add(buttonBox);
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        setCenter(root);
    }

    /**
     *
     * @return all answers that are not "unknown"
     */
    public List<PhenoAnswer> getAnswers() {
        return phenoqTable.getItems().stream()
                .filter(Predicate.not(q -> q.phenoAnswer().unknown()))
                .map(Qphenorow::phenoAnswer)
                .collect(Collectors.toList());
    }



}
