package org.monarchinitiative.fenominal.gui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.controlsfx.control.spreadsheet.GridBase;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType;
import org.controlsfx.control.spreadsheet.SpreadsheetView;
import org.controlsfx.dialog.CommandLinksDialog;
import org.monarchinitiative.fenominal.core.FenominalRunTimeException;
import org.monarchinitiative.fenominal.gui.guitools.MiningTask;
import org.monarchinitiative.fenominal.gui.guitools.PopUps;
import org.monarchinitiative.fenominal.gui.io.HpoMenuDownloader;
import org.monarchinitiative.fenominal.gui.model.CaseReport;
import org.monarchinitiative.fenominal.gui.model.TextMiningResultsModel;
import org.monarchinitiative.hpotextmining.gui.controller.HpoTextMining;
import org.monarchinitiative.hpotextmining.gui.controller.Main;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import static org.monarchinitiative.fenominal.gui.guitools.MiningTask.CaseReport;
import static org.monarchinitiative.fenominal.gui.guitools.MiningTask.UNINITIALIZED;

@Component
public class FenominalMainController {

    @FXML
    public Button parseButton;

    @FXML
    public Button outputButton;
    @FXML
    public Button setupButton;

    @FXML public Button importTextFile;

    @FXML
    public Label hpoReadyLabel;

    @FXML
    public SpreadsheetView spreadSheetView;


    private FenominalMiner fenominalMiner = null;
    private final ExecutorService executor;

    private final OptionalResources optionalResources;

    private final Properties pgProperties;

    private final File appHomeDirectory;

    private MiningTask miningTaskType = UNINITIALIZED;

    private TextMiningResultsModel model = null;

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
        this.setupButton.disableProperty().bind(optionalResources.ontologyProperty().isNull());
        // set up table view with
        int rowCount = 6;
        int columnCount = 2;
        GridBase grid = new GridBase(rowCount, columnCount);
        ObservableList<ObservableList<SpreadsheetCell>> rows = FXCollections.observableArrayList();
        for (int row = 0; row < grid.getRowCount(); ++row) {
            final ObservableList<SpreadsheetCell> list = FXCollections.observableArrayList();
            for (int column = 0; column < grid.getColumnCount(); ++column) {
                list.add(SpreadsheetCellType.STRING.createCell(row, column, 1, 1,"value"));
            }
            rows.add(list);
        }
        grid.setRows(rows);
        this.spreadSheetView.setGrid(grid);
        //
        this.parseButton.setDisable(true);
        this.outputButton.setDisable(true);
    }



    @FXML
    private void parseButtonPressed(ActionEvent e) {
        Ontology ontology = this.optionalResources.getOntology();
        if (ontology == null) {
            PopUps.showInfoMessage("Need to set location to hp.json ontology file first! (See edit menu)", "Error");
            return;
        }
        this.fenominalMiner = new FenominalMiner(ontology);
        try {
            HpoTextMining hpoTextMining = HpoTextMining.builder()
                    .withTermMiner(this.fenominalMiner)
                    .withOntology(this.fenominalMiner.getHpo())
                    .withExecutorService(executor)
                    .withPhenotypeTerms(new HashSet<>()) // maybe you want to display some terms from the beginning
                    .build();
            // get reference to primary stage
            Window w = this.parseButton.getScene().getWindow();

            // show the text mining analysis dialog in the new stage/window
            Stage secondary = new Stage();
            secondary.initOwner(w);
            secondary.setTitle("HPO text mining analysis");
            secondary.setScene(new Scene(hpoTextMining.getMainParent()));
            secondary.showAndWait();

            Set<Main.PhenotypeTerm> approved = hpoTextMining.getApprovedTerms();
            switch (this.miningTaskType) {
                case CaseReport:
                    model = new CaseReport(approved);
                    model.output();
                    break;
                default:
                    PopUps.showInfoMessage("Error, minig task not implemented yet", "Error");
                    return;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }


        e.consume();
    }


    private void initCaseReport() {
        System.out.println("case report");
        this.parseButton.setDisable(false);
        this.parseButton.setText("Mine case report");
        this.miningTaskType = CaseReport;
    }




    @FXML
    private void getStarted(ActionEvent e) {
        var caseReport = new CommandLinksDialog.CommandLinksButtonType("Case report","Enter data about one individual, one time point", true);
        var caseReportTemporal = new CommandLinksDialog.CommandLinksButtonType("Case report (multiple time points)","Enter data about one individual, multiple time points", false);
        var cohortTogether = new CommandLinksDialog.CommandLinksButtonType("Cohort","Enter data about cohort", false);
        var cohortOneByOne = new CommandLinksDialog.CommandLinksButtonType("Cohort (enter data about multiple individuals)","Enter data about cohort (one by one)", false);
        var cancel = new CommandLinksDialog.CommandLinksButtonType("Cancel","Cancel", false);
        CommandLinksDialog dialog = new CommandLinksDialog(caseReport, caseReportTemporal, cohortTogether, cohortOneByOne, cancel);
        dialog.setTitle("Get started");
        dialog.setHeaderText("Select type of curation");
        dialog.setContentText("Fenominal supports four types of HPO biocuration. This will delete current work (Cancel to return).");
        Optional<ButtonType> opt = dialog.showAndWait();
        if (opt.isPresent()) {
            ButtonType btype = opt.get();
            switch(btype.getText()) {
                case "Case report":
                    initCaseReport();
                    break;
                case "Case report (multiple time points)":
                    System.out.println("case report temporal");
                    break;
                case "Cohort":
                    System.out.println("cohortTogether");
                    break;
                case "Cohort (enter data about multiple individuals)":
                    System.out.println("cohortOneByOne");
                    break;
                case "Cancel":
                default:
                    return;
            }
        }
        e.consume();
    }

    @FXML
    private void importHpJson(ActionEvent e) {

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
