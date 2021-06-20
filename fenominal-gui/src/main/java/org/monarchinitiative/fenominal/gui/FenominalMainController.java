package org.monarchinitiative.fenominal.gui;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.controlsfx.dialog.CommandLinksDialog;
import org.monarchinitiative.fenominal.core.FenominalRunTimeException;
import org.monarchinitiative.fenominal.gui.guitools.DataEntryPane;
import org.monarchinitiative.fenominal.gui.guitools.MiningTask;
import org.monarchinitiative.fenominal.gui.guitools.PopUps;
import org.monarchinitiative.fenominal.gui.io.HpoMenuDownloader;
import org.monarchinitiative.fenominal.gui.model.CaseReport;
import org.monarchinitiative.fenominal.gui.model.FenominalTerm;
import org.monarchinitiative.fenominal.gui.model.OneByOneCohort;
import org.monarchinitiative.fenominal.gui.model.TextMiningResultsModel;
import org.monarchinitiative.hpotextmining.gui.controller.HpoTextMining;
import org.monarchinitiative.hpotextmining.gui.controller.Main;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import static org.monarchinitiative.fenominal.gui.guitools.MiningTask.*;

@SuppressWarnings({"unchecked", "rawtypes"})
@Component
public class FenominalMainController {

    @FXML
    public Button parseButton;

    @FXML
    public Button outputButton;
    @FXML
    public Button setupButton;

    @FXML
    public Label hpoReadyLabel;

    @FXML
    public TableView metaDataTableView;
    /** We hide the table until the first bits of data are entered. */
    private final BooleanProperty tableHidden;


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
        this.tableHidden = new SimpleBooleanProperty(true);
    }


    public void initialize() {
        // run the initialization task on a separate thread
        StartupTask task = new StartupTask(optionalResources, pgProperties);
        this.hpoReadyLabel.textProperty().bind(task.messageProperty());
        task.setOnSucceeded(e -> this.hpoReadyLabel.textProperty().unbind());
        this.executor.submit(task);
        // only enable analyze if Ontology downloaded (enabled property watches
        this.setupButton.disableProperty().bind(optionalResources.ontologyProperty().isNull());
        this.parseButton.setDisable(true);
        this.outputButton.setDisable(true);
        // set up table view
        TableColumn<Map, String> itemColumn = new TableColumn<>("item");
        itemColumn.setCellValueFactory(new MapValueFactory<>("item"));
        TableColumn<Map, String> valueColumn = new TableColumn<>("value");
        valueColumn.setCellValueFactory(new MapValueFactory<>("value"));
        this.metaDataTableView.getColumns().add(itemColumn);
        this.metaDataTableView.getColumns().add(valueColumn);
        this.metaDataTableView.setColumnResizePolicy( TableView.CONSTRAINED_RESIZE_POLICY );
        itemColumn.setMaxWidth( 1f * Integer.MAX_VALUE * 15 );
        valueColumn.setMaxWidth( 1f * Integer.MAX_VALUE * 85 );
        // TODO -- Want to get the table to disappear until we have done the "get started"
        // The following causes the table to disappear, but setting the value of hidden to false does
        // not cause it to reappear
//        this.metaDataTableView.visibleProperty().bind(this.tableHiddenProperty());
//        this.metaDataTableView.managedProperty().bind(this.tableHiddenProperty().not());
        // Ordered map of data for the table
        Map<String, String> mp = new LinkedHashMap<>();
        String versionInfo = getHpoVersion();
        mp.put("HPO", versionInfo);
        // TODO -- for some reason, the Ontology version is not available here.
      //  populateTableWithData(mp);
    }

    private BooleanProperty tableHiddenProperty() {
        return this.tableHidden;
    }

    private void populateTableWithData(Map<String, String> data) {
        this.metaDataTableView.getItems().clear();
        ObservableList<Map<String, Object>> itemMap = FXCollections.observableArrayList();
        for (Map.Entry<String, String> e :  data.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("item", e.getKey());
            item.put("value" , e.getValue());
            itemMap.add(item);
        }
        this.metaDataTableView.getItems().addAll(itemMap);
        this.tableHiddenProperty().set(false);
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
            List<FenominalTerm> approvedTerms = approved.stream()
                    .map(FenominalTerm::fromMainPhenotypeTerm)
                    .sorted()
                    .collect(Collectors.toList());
            model.addHpoFeatures(approvedTerms);
            switch (this.miningTaskType) {
                case CASE_REPORT:
                    model.output();
                    break;
                case COHORT_ONE_BY_ONE:
                    model.output();
                    int casesSoFar = model.minedSoFar();
                    this.parseButton.setText(String.format("Mine case report %d", casesSoFar+1));
                    break;
                default:
                    PopUps.showInfoMessage("Error, mining task not implemented yet", "Error");
                    return;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        updateTable();
        // if we get here, we have data that could be output
        this.outputButton.setDisable(false);
        e.consume();
    }

    private void updateTable() {
        Map<String, String> data = new LinkedHashMap<>();
        data.put("HPO", getHpoVersion());
        data.put("patients (n)",  String.valueOf(model.minedSoFar()));
        data.put("terms curated (n)", String.valueOf(model.getTermCount()));
        populateTableWithData(data);
    }

    /**
     * @return Version of HPO being used for curation, corresponding to the data-version attribute in hp.json
     */
    private String getHpoVersion() {
        Ontology hpo = optionalResources.getOntology();
        if (hpo != null) {
            return hpo.getMetaInfo().getOrDefault("data-version", "n/a");
        } else {
            return  "not initialized";
        }
    }


    private void initCaseReport() {
        DataEntryPane dataEntryPane = new DataEntryPane();
        GridPane gridPane = dataEntryPane.getPane();
        Scene scene = new Scene(gridPane, 800, 500);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.showAndWait();
        if (dataEntryPane.isValid()) {
            System.out.println(dataEntryPane.getIdentifier() + ":" + dataEntryPane.getYears() + ": " + dataEntryPane.getMonths());
            Map<String, String> mp = new LinkedHashMap<>();
            mp.put("HPO",  getHpoVersion());
            mp.put("id", dataEntryPane.getIdentifier());
            mp.put("age", String.format("%d years, %d months", dataEntryPane.getYears(), dataEntryPane.getMonths()));
            populateTableWithData(mp);
            this.parseButton.setDisable(false);
            this.parseButton.setText("Mine case report");
            this.miningTaskType = CASE_REPORT;
            this.model = new CaseReport();
        } else {
            PopUps.showInfoMessage("Could not initialize case report", "Error");
        }
    }


    /**
     * Set up parsing for a small cohort whose clinical data we enter one by one. The purpose of this is
     * for papers that describe say 2-10 patients with a disease, and we would like to generate n/m frequencies
     * for the HPO terms.
     */
    private void initCohortOneByOne() {
        System.out.println("cohortOneByOne");
        Map<String, String> mp = new LinkedHashMap<>();
        mp.put("HPO",  getHpoVersion());
        mp.put("Curated so far", "0");
        populateTableWithData(mp);
        this.parseButton.setDisable(false);
        this.parseButton.setText("Mine case report 1");
        this.miningTaskType = COHORT_ONE_BY_ONE;
        this.model = new OneByOneCohort();
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
                    initCohortOneByOne();
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

    public void outputButtonPressed(ActionEvent actionEvent) {
        String initialFilename = "fenomimal.txt";
        FileChooser fileChooser = new FileChooser();
        Stage stage = (Stage) this.outputButton.getScene().getWindow();
        //String defaultdir = settings.getDefaultDirectory();
        //Set extension filter
       // FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TAB/TSV files (*.tab)", "*.tab");
        //fileChooser.getExtensionFilters().add(extFilter);
      //  fileChooser.setInitialFileName(this.currentPhenoteFileBaseName);
       // fileChooser.setInitialDirectory(new File(defaultdir));
        //Show save file dialog
        fileChooser.setInitialFileName(initialFilename);
        File file = fileChooser.showSaveDialog(stage);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write(model.getTsv());
        } catch (IOException e) {
            PopUps.showInfoMessage("Could not write to file: " + e.getMessage(), "IO Error");
        }
    }
}
