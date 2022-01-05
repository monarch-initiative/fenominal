package org.monarchinitiative.fenominal.gui.controller;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.controlsfx.dialog.CommandLinksDialog;
import org.monarchinitiative.fenominal.core.FenominalRunTimeException;
import org.monarchinitiative.fenominal.gui.FenominalMinerApp;
import org.monarchinitiative.fenominal.gui.OptionalResources;
import org.monarchinitiative.fenominal.gui.StartupTask;
import org.monarchinitiative.fenominal.gui.guitools.*;
import org.monarchinitiative.fenominal.gui.io.HpoMenuDownloader;
import org.monarchinitiative.fenominal.gui.model.*;
import org.monarchinitiative.fenominal.gui.output.*;
import org.monarchinitiative.hpotextmining.gui.controller.HpoTextMining;
import org.monarchinitiative.hpotextmining.gui.controller.Main;
import org.monarchinitiative.phenofx.questionnnaire.PhenoQuestionnaire;
import org.monarchinitiative.phenofx.questionnnaire.phenoitem.PhenoAnswer;
import org.monarchinitiative.phenol.base.PhenolRuntimeException;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.bind.validation.ValidationBindHandler;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import static org.monarchinitiative.fenominal.gui.OptionalResources.BIOCURATOR_ID_PROPERTY;
import static org.monarchinitiative.fenominal.gui.guitools.MiningTask.*;

@SuppressWarnings({"unchecked", "rawtypes"})
@Component
public class FenominalMainController {
    private static final Logger LOGGER = LoggerFactory.getLogger(FenominalMainController.class);
    @FXML
    public Button parseButton;

    @FXML
    public Button outputButton;
    @FXML
    public Button setupButton;

    @FXML
    private Button previwButton;

    @FXML
    public Label hpoReadyLabel;

    @FXML
    public TableView metaDataTableView;
    /** We hide the table until the first bits of data are entered. */
    private final BooleanProperty tableHidden;


    private final ExecutorService executor;

    private final OptionalResources optionalResources;

    private final Properties pgProperties;

    private final File appHomeDirectory;

    private MiningTask miningTaskType = UNINITIALIZED;

    private TextMiningResultsModel model = null;

    @Autowired
    ResourceLoader resourceLoader;

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
        this.previwButton.setDisable(true);
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
        // Ordered map of data for the table
        Map<String, String> mp = new LinkedHashMap<>();
        String versionInfo = getHpoVersion();
        mp.put("HPO", versionInfo);
        populateTableWithData(mp);
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
        LOGGER.error("Parse button pressed" );

        Ontology ontology = this.optionalResources.getOntology();
        if (ontology == null) {
            PopUps.showInfoMessage("Need to set location to hp.json ontology file first! (See edit menu)", "Error");
            return;
        }
        LocalDate encounterDate = null;
        String isoAge = null;
        if (this.miningTaskType == PHENOPACKET) {
            PhenopacketModel pmodel = (PhenopacketModel) this.model;
            DatePickerDialog dialog = DatePickerDialog.getEncounterDate(pmodel.getBirthdate(), pmodel.getEncounterDates());
            encounterDate = dialog.showDatePickerDialog();
        } else if (this.miningTaskType == PHENOPACKET_BY_AGE) {
            PhenopacketByAgeModel pAgeModel = (PhenopacketByAgeModel) this.model;
            AgePickerDialog agePickerDialog = new AgePickerDialog(pAgeModel.getEncounterAges());
            isoAge = agePickerDialog.showAgePickerDialog();
        }
        FenominalMinerApp fenominalMiner = new FenominalMinerApp(ontology);
           try {
            HpoTextMining hpoTextMining = HpoTextMining.builder()
                    .withTermMiner(fenominalMiner)
                    .withOntology(fenominalMiner.getHpo())
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
            switch (this.miningTaskType) {
                case CASE_REPORT:
                    model.addHpoFeatures(approvedTerms);
                    break;
                case COHORT_ONE_BY_ONE:
                    model.addHpoFeatures(approvedTerms);
                    int casesSoFar = model.casesMined();
                    this.parseButton.setText(String.format("Mine case report %d", casesSoFar+1));
                    break;
                case PHENOPACKET:
                    int encountersSoFar = model.casesMined();
                    this.parseButton.setText(String.format("Mine encounter %d", encountersSoFar+1));
                    model.addHpoFeatures(approvedTerms, encounterDate);
                    break;
                case PHENOPACKET_BY_AGE:
                    encountersSoFar = model.casesMined();
                    this.parseButton.setText(String.format("Mine encounter %d", encountersSoFar+1));
                    model.addHpoFeatures(approvedTerms, isoAge);
                    break;
                default:
                    PopUps.showInfoMessage("Error, mining task not implemented yet", "Error");
                    return;
            }
        } catch (IOException ex) {
            PopUps.showException("Error", "HPO textmining error", ex.getMessage(), ex);
            LOGGER.error("Error doing HPO Textming: {}", ex.getMessage());
        }
        updateTable();
        // if we get here, we have data that could be output
        this.previwButton.setDisable(false);
        this.outputButton.setDisable(false);
        e.consume();
    }

    private void updateTable() {
        Map<String, String> data = new LinkedHashMap<>();
        data.put("HPO", getHpoVersion());
        data.put("patients (n)",  String.valueOf(model.casesMined()));
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
        CaseDataEntryPane dataEntryPane = new CaseDataEntryPane();
        dataEntryPane.showAgePickerDialog();
        String isoAge = dataEntryPane.getIsoAge();
        String id = dataEntryPane.getCaseId();
        Map<String, String> mp = new LinkedHashMap<>();
        mp.put("HPO",  getHpoVersion());
        mp.put("id", id);
        mp.put("age", isoAge);
        populateTableWithData(mp);
        this.parseButton.setDisable(false);
        this.parseButton.setText("Mine case report");
        this.miningTaskType = CASE_REPORT;
        this.model = new CaseReport(id, isoAge);
    }


    /**
     * Set up parsing for a small cohort whose clinical data we enter one by one. The purpose of this is
     * for papers that describe say 2-10 patients with a disease, and we would like to generate n/m frequencies
     * for the HPO terms.
     */
    private void initCohortOneByOne() {
        if (pgProperties.getProperty(BIOCURATOR_ID_PROPERTY) == null) {
            PopUps.showInfoMessage("Please enter your biocurator ID (edit menu) before entering cohort data", "Error");
            return;
        }
        CohortDataPane dataPane = new CohortDataPane();
        dataPane.showDataEntryPane();
        String pmid = dataPane.getPmid();
        String omimId = dataPane.getOmimId();
        String diseasename = dataPane.getDiseaseName();
        try {
            TermId tid = TermId.of(omimId);
        } catch (PhenolRuntimeException e ) {
            PopUps.showException("Error", "Could not parse OMIM id", "Please start again, OMIM id should be like OMIM:600123", e);
            return;
        }
        Map<String, String> mp = new LinkedHashMap<>();
        mp.put("HPO",  getHpoVersion());
        mp.put("Curated so far", "0");
        mp.put("OMIM id", omimId);
        mp.put("Disease", diseasename);
        mp.put("PMID", pmid);
        populateTableWithData(mp);
        this.parseButton.setDisable(false);
        this.parseButton.setText("Mine case report 1");
        this.miningTaskType = COHORT_ONE_BY_ONE;
        this.model = new OneByOneCohort(pmid, omimId, diseasename);
    }

    /**
     * Set up parsing for a single individual over one or more time points with the goal of outputting a
     * GA4GH phenopacket with one or multiple time points
     */
    private void initPhenopacket() {
        Map<String, String> mp = new LinkedHashMap<>();
        mp.put("HPO",  getHpoVersion());
        mp.put("Curated so far", "0");
        populateTableWithData(mp);
        this.parseButton.setDisable(false);
        this.parseButton.setText("Mine time point 1");
        this.miningTaskType = PHENOPACKET;
        DatePickerDialog dialog = DatePickerDialog.getBirthDate();
        LocalDate bdate = dialog.showDatePickerDialog();
        this.model = new PhenopacketModel(bdate);
    }

    private void initPhenopacketWithManualAge() {
        Map<String, String> mp = new LinkedHashMap<>();
        mp.put("HPO",  getHpoVersion());
        mp.put("Curated so far", "0");
        populateTableWithData(mp);
        this.parseButton.setDisable(false);
        this.parseButton.setText("Mine encounter 1");
        this.miningTaskType = PHENOPACKET_BY_AGE;
        this.model = new PhenopacketByAgeModel();
    }


    @FXML
    private void getStarted(ActionEvent e) {
        var caseReport = new CommandLinksDialog.CommandLinksButtonType("Case report","Enter data about one individual, one time point", true);
        var phenopacketByBirthDate = new CommandLinksDialog.CommandLinksButtonType("Phenopacket","Enter data about one individual, multiple time points", false);
        var cohortTogether = new CommandLinksDialog.CommandLinksButtonType("Cohort","Enter data about cohort", false);
        var phenopacketByIso8601Age = new CommandLinksDialog.CommandLinksButtonType("Phenopacket (by age at encounter)","Enter data about one individual, multiple ages", false);
        var cancel = new CommandLinksDialog.CommandLinksButtonType("Cancel","Cancel", false);
        CommandLinksDialog dialog = new CommandLinksDialog(phenopacketByBirthDate, phenopacketByIso8601Age, caseReport, cohortTogether, cancel);
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
                case "Phenopacket":
                    initPhenopacket();
                    break;
                case "Phenopacket (by age at encounter)":
                    initPhenopacketWithManualAge();
                    break;
                case "Cohort":
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

    @FXML
    public void outputButtonPressed(ActionEvent actionEvent) {
        String initialFilename = "fenomimal.txt";
        FileChooser fileChooser = new FileChooser();
        Stage stage = (Stage) this.outputButton.getScene().getWindow();
        fileChooser.setInitialFileName(initialFilename);
        File file = fileChooser.showSaveDialog(stage);
        if (file == null) {
            PopUps.showInfoMessage("Could not retrieve file name, using default (\" fenomimal.txt\"", "Error");
            file = new File(initialFilename);
        }
        try (Writer writer = new BufferedWriter(new FileWriter(file))) {
            PhenoOutputter phenoOutputter;

            switch (this.miningTaskType) {
                case CASE_REPORT -> phenoOutputter = new CaseReportTsvOutputter((CaseReport) this.model);
                case COHORT_ONE_BY_ONE -> {
                    String biocuratorId = pgProperties.getProperty(BIOCURATOR_ID_PROPERTY);
                    phenoOutputter = new PhenoteFxTsvOutputter((OneByOneCohort) this.model, biocuratorId);
                }
                case PHENOPACKET -> phenoOutputter = new PhenopacketJsonOutputter((PhenopacketModel) this.model);
                case PHENOPACKET_BY_AGE -> phenoOutputter = new PhenopacketByAgeJsonOutputter((PhenopacketByAgeModel) this.model);
                default -> phenoOutputter = new ErrorOutputter();
            }
            phenoOutputter.output(writer);
        } catch (IOException e) {
            PopUps.showInfoMessage("Could not write to file: " + e.getMessage(), "IO Error");
        }
    }

    @FXML
    public void previewOutput(ActionEvent e) {
        PhenoOutputter phenoOutputter;
        Writer writer = new StringWriter();
        phenoOutputter = switch (this.miningTaskType) {
            case CASE_REPORT -> new CaseReportTsvOutputter((CaseReport) this.model);
            case COHORT_ONE_BY_ONE -> new PhenoteFxTsvOutputter((OneByOneCohort) this.model, pgProperties.getProperty(BIOCURATOR_ID_PROPERTY));
            case PHENOPACKET -> new PhenopacketJsonOutputter((PhenopacketModel) this.model);
            case PHENOPACKET_BY_AGE -> new PhenopacketByAgeJsonOutputter((PhenopacketByAgeModel) this.model);
            default -> new ErrorOutputter();
        };
        try {
            phenoOutputter.output(writer);
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
        }
        Text text1 = new Text(writer.toString());
        text1.setFill(Color.BLUE);
        text1.setFont(Font.font("Helvetica", FontPosture.REGULAR, 14));
        TextFlow textFlow = new TextFlow(text1);
        Stage stage = new Stage();
        Scene testScene = new Scene(textFlow);
        stage.setScene(testScene);
        stage.showAndWait();
        e.consume();
    }


    @FXML
    void setBiocuratorMenuItemClicked(ActionEvent event) {
        String biocurator = PopUps.getStringFromUser("Biocurator ID",
                "e.g., HPO:rrabbit", "Enter your biocurator ID:");
        if (biocurator != null) {
            this.pgProperties.setProperty(BIOCURATOR_ID_PROPERTY, biocurator);
            PopUps.showInfoMessage(String.format("Biocurator ID set to \n\"%s\"",
                    biocurator), "Success");
        } else {
            PopUps.showInfoMessage("Biocurator ID not set.",
                    "Information");
        }
        event.consume();
    }

    @FXML
    private void questionnaire(ActionEvent e) {
        e.consume();
        Resource questionnaireResource = resourceLoader.getResource("classpath:questionnaire.fxml");
        if (! questionnaireResource.exists()) {
            LOGGER.error("Could not find Questionnare FXML resource (fxml file not found)");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(questionnaireResource.getURL()));
            QuestionnaireController qcontoller = new QuestionnaireController(this.optionalResources.getOntology());
            loader.setController(qcontoller);
            VBox parent = loader.load();
            Ontology hpo = this.optionalResources.getOntology();
            PhenoQuestionnaire pq = PhenoQuestionnaire.development(hpo);
            qcontoller.setQuestions(pq.getQuestions());
            Scene scene = new Scene(parent, 800, 600);
            Stage secondary = new Stage();
            secondary.setTitle("PhenoQuestionnaire");
            secondary.setScene(scene);
            secondary.showAndWait();
            List<PhenoAnswer> answers = qcontoller.getAnswers();
            // Transform the PhenoAnswer objects into FenominalTerm objects to add them to our model
            List<FenominalTerm> fterms = new ArrayList<>();
            for (PhenoAnswer answer : answers) {
                if (answer.unknown()) {
                    LOGGER.error("Unknown Phenoanswer passed to controller (should never happen");
                    continue;
                }
                fterms.add(new FenominalTerm(answer.term(), answer.observed()));
            }
            model.addHpoFeatures(fterms);
        } catch (IOException ex) {
            LOGGER.error("Could not load questionnaire {}", ex.getMessage());
            PopUps.showException("Error", "Could not load Questionnaire", ex.getMessage(), ex);
        }

    }


}
