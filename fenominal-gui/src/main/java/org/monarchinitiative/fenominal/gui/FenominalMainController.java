package org.monarchinitiative.fenominal.gui;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.monarchinitiative.hpotextmining.gui.controller.HpoTextMining;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Component
public class FenominalMainController {



    @FXML
    public TextArea parseArea;
    @FXML
    public Button parseButton;
    @FXML
    public Button pasteClipboard;
    @FXML public Button importTextFile;
    @FXML public Button importHpObo;
    @FXML
    public Label hpoReadyLabel;
    private FenominalMiner fenominalMiner = null;
    private final ExecutorService executor;

    private final OptionalResources optionalResources;

    public FenominalMainController(OptionalResources optionalResources, ExecutorService executorService) {
        this.optionalResources = optionalResources;
        this.executor = executorService;

    }


    public void initialize() {
        // only enable analyze if Ontology downloaded (enabled property watches
        parseButton.disableProperty().bind(optionalResources.ontologyProperty().isNull());
        optionalResources.ontologyProperty().addListener((observable,oldValue,newValue) -> { if (newValue!=null) {
            this.hpoReadyLabel.setText("");
        }
       });
    }



    @FXML
    private void parseButtonPressed(ActionEvent e) {
        //String contents = parseArea.getText();
        if (this.fenominalMiner == null) {
            System.err.println("[ERROR] hp.obo not initialized");
            return;
        }
        try {
            HpoTextMining hpoTextMining = HpoTextMining.builder()
                    .withTermMiner(this.fenominalMiner)
                    .withOntology(this.fenominalMiner.getHpo())
                    .withExecutorService(executor)
                    .withPhenotypeTerms(new HashSet<>()) // maybe you want to display some terms from the beginning
                    .build();
            // get reference to primary stage
            Window w = this.parseArea.getScene().getWindow();

            // show the text mining analysis dialog in the new stage/window
            Stage secondary = new Stage();
            secondary.initOwner(w);
            secondary.setTitle("HPO text mining analysis");
            secondary.setScene(new Scene(hpoTextMining.getMainParent()));
            secondary.showAndWait();

            // do something with the results
            System.out.println(hpoTextMining.getApprovedTerms().stream()
                    .map(Object::toString)
                    .collect(Collectors.joining("\n", "Approved terms:\n", "")));
        } catch (IOException ex) {
            ex.printStackTrace();
        }


        e.consume();
    }
    @FXML
    private void pasteClipboard(ActionEvent e) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        if (clipboard.hasString()) {
            this.parseArea.setText(clipboard.getString());
        }
        e.consume();
    }

    @FXML
    private void importHpObo(ActionEvent e) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open hp.json");
        FileChooser.ExtensionFilter jsonFilter
                = new FileChooser.ExtensionFilter("JSON Files", "*.json");
        fileChooser.getExtensionFilters().add(jsonFilter);
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            //File hpJsonFile = new FenominalMiner(file.getAbsolutePath());
            //this.optionalResources.
        }
    }

    @FXML
    private void quitApplication(ActionEvent e) {
        e.consume();
        Platform.exit();

    }
}
