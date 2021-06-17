package org.monarchinitiative.fenominal.gui;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.monarchinitiative.fenominal.core.FenominalRunTimeException;
import org.monarchinitiative.fenominal.gui.guitools.PopUps;
import org.monarchinitiative.fenominal.gui.io.HpoMenuDownloader;
import org.monarchinitiative.hpotextmining.gui.controller.HpoTextMining;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;
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

    @FXML
    public Label hpoReadyLabel;
    private FenominalMiner fenominalMiner = null;
    private final ExecutorService executor;

    private final OptionalResources optionalResources;

    private final Properties pgProperties;

    private final File appHomeDirectory;

    @Autowired
    public FenominalMainController(OptionalResources optionalResources,
                                   ExecutorService executorService,
                                   Properties pgProperties,
                                   @Qualifier("appHomeDir") File appHomeDir) {
        this.optionalResources = optionalResources;
        this.executor = executorService;
        this.pgProperties = pgProperties;
        this.appHomeDirectory = appHomeDir;
    }


    public void initialize() {
        // run the initialization task on a separate thread
        StartupTask task = new StartupTask(optionalResources, pgProperties);
        this.hpoReadyLabel.textProperty().bind(task.messageProperty());
        task.setOnSucceeded(e -> this.hpoReadyLabel.textProperty().unbind());
        this.executor.submit(task);
        // only enable analyze if Ontology downloaded (enabled property watches
        parseButton.disableProperty().bind(optionalResources.ontologyProperty().isNull());
//        optionalResources.ontologyProperty().addListener((observable,oldValue,newValue) -> { if (newValue!=null) {
//            this.hpoReadyLabel.setText("");
//        }
//       });
    }



    @FXML
    private void parseButtonPressed(ActionEvent e) {
        Ontology ontology = this.optionalResources.getOntology();
        if (ontology == null) {
            PopUps.showInfoMessage("Need to set location to hp.json ontology file first! (See edit menu)", "Error");
            return;
        }
        this.fenominalMiner = new FenominalMiner(ontology);
        if (this.fenominalMiner == null) {
            System.err.println("[ERROR] hp.json not initialized");
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
    private void importHpJson(ActionEvent e) {
        //this.optionalResources.
        String fname = this.appHomeDirectory.getAbsolutePath() + File.separator + OptionalResources.DEFAULT_HPO_FILE_NAME;
        HpoMenuDownloader downloader = new HpoMenuDownloader();
        try {
            downloader.downloadHpo(fname);
            pgProperties.setProperty(OptionalResources.ONTOLOGY_PATH_PROPERTY, fname);
        } catch (FenominalRunTimeException ex) {
            ex.printStackTrace();
        }

    }

    @FXML
    private void quitApplication(ActionEvent e) {
        e.consume();
        Platform.exit();

    }
}
