package org.monarchinitiative.fenominal.gui.controller;

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
import org.monarchinitiative.fenominal.gui.config.ApplicationProperties;
import org.monarchinitiative.fenominal.gui.guitools.*;
import org.monarchinitiative.fenominal.gui.hpotextminingwidget.HpoTextMining;
import org.monarchinitiative.fenominal.gui.hpotextminingwidget.PhenotypeTerm;
import org.monarchinitiative.fenominal.gui.io.HpoMenuDownloader;
import org.monarchinitiative.fenominal.gui.io.PhenopacketImporter;
import org.monarchinitiative.fenominal.gui.model.*;
import org.monarchinitiative.fenominal.gui.output.*;
import org.monarchinitiative.fenominal.gui.questionnaire.PhenoQuestionnaire;
import org.monarchinitiative.fenominal.gui.questionnaire.QuestionnairePane;
import org.monarchinitiative.fenominal.gui.questionnaire.phenoitem.PhenoAnswer;
import org.monarchinitiative.phenol.base.PhenolRuntimeException;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import static org.monarchinitiative.fenominal.gui.config.FenominalConfig.*;
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
    public Button questionnaireButtn;
    @FXML
    private Button updatePhenopacketButton;

    @FXML
    private Button previwButton;

    @FXML
    public Label hpoReadyLabel;

    @FXML
    public TableView metaDataTableView;
    /**
     * We hide the table until the first bits of data are entered.
     */
    private final BooleanProperty tableHidden;


    private final ExecutorService executor;

    private final OptionalResources optionalResources;

    private final Properties pgProperties;

    private final File appHomeDirectory;

    private MiningTask miningTaskType = UNINITIALIZED;

    private TextMiningResultsModel model = null;

    @Autowired
    ApplicationProperties applicationProperties;


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
        StartupTask task = new StartupTask(optionalResources, pgProperties, this.appHomeDirectory);
        this.hpoReadyLabel.textProperty().bind(task.messageProperty());
        task.setOnSucceeded(e -> this.hpoReadyLabel.textProperty().unbind());
        this.executor.submit(task);
        // only enable analyze if Ontology downloaded (enabled property watches
        this.setupButton.disableProperty().bind(optionalResources.ontologyProperty().isNull());
        this.parseButton.setDisable(true);
        this.previwButton.setDisable(true);
        this.outputButton.setDisable(true);
        this.questionnaireButtn.setDisable(true);
        Platform.runLater(() ->{
            Scene scene = this.parseButton.getScene();
            scene.getWindow().setOnCloseRequest(ev -> {
                if (!shutdown()) {
                    ev.consume();
                }
            });
        });

        // set up table view
        TableColumn<Map, String> itemColumn = new TableColumn<>("item");
        itemColumn.setCellValueFactory(new MapValueFactory<>("item"));
        TableColumn<Map, String> valueColumn = new TableColumn<>("value");
        valueColumn.setCellValueFactory(new MapValueFactory<>("value"));
        this.metaDataTableView.getColumns().add(itemColumn);
        this.metaDataTableView.getColumns().add(valueColumn);
        this.metaDataTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        itemColumn.setMaxWidth(1f * Integer.MAX_VALUE * 25);
        valueColumn.setMaxWidth(1f * Integer.MAX_VALUE * 75);
        // Ordered map of data for the table
        Map<String, String> mp = new LinkedHashMap<>();
        String versionInfo = getHpoVersion();
        mp.put(HPO_VERSION_KEY, versionInfo);
        populateTableWithData(mp);
    }

    private BooleanProperty tableHiddenProperty() {
        return this.tableHidden;
    }

    private void populateTableWithData(Map<String, String> data) {
        this.metaDataTableView.getItems().clear();
        ObservableList<Map<String, Object>> itemMap = FXCollections.observableArrayList();
        for (Map.Entry<String, String> e : data.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("item", e.getKey());
            item.put("value", e.getValue());
            itemMap.add(item);
        }
        this.metaDataTableView.getItems().addAll(itemMap);
        this.tableHiddenProperty().set(false);
    }

    /**
     * Calculate the age of the patient as a Java Period object
     *
     * @param birthdate     birth date
     * @param encounterDate data at which the phenotype was first observed
     * @return a Period object representing the patient age
     */
    private Period getAge(LocalDate birthdate, LocalDate encounterDate) {
        return Period.between(birthdate, encounterDate);
    }

    @FXML
    private void parseButtonPressed(ActionEvent e) {
        LOGGER.trace("Parse button pressed");

        Ontology ontology = this.optionalResources.getOntology();
        if (ontology == null) {
            PopUps.showInfoMessage("Need to set location to hp.json ontology file first! (See edit menu)", "Error");
            return;
        }
        LocalDate encounterDate = null;
        String isoAge = null;
        if (this.miningTaskType == PHENOPACKET) {
            PhenopacketModel pmodel = (PhenopacketModel) this.model;
            Optional<LocalDate> bdOpt = pmodel.getBirthdate();
            if (bdOpt.isEmpty()) {
                PopUps.showInfoMessage("Error", "Cannot enter phenotype info without birthdate");
                return; // should never happen
            }
            DatePickerDialog dialog = DatePickerDialog.getEncounterDate(bdOpt.get(), pmodel.getEncounterDates(bdOpt.get()));
            encounterDate = dialog.showDatePickerDialog();
        } else if (this.miningTaskType == PHENOPACKET_BY_AGE) {
            PhenopacketByAgeModel pAgeModel = (PhenopacketByAgeModel) this.model;
            AgePickerDialog agePickerDialog = new AgePickerDialog(pAgeModel.getEncounterAges());
            isoAge = agePickerDialog.showAgePickerDialog();
        }
        FenominalMinerApp fenominalMiner = new FenominalMinerApp(ontology);
        HpoTextMining hpoTextMining = HpoTextMining.builder()
                .withExecutorService(executor)
                .withOntology(fenominalMiner.getHpo())
                .withTermMiner(fenominalMiner)
                .build();
        // get reference to primary stage
        Window w = this.parseButton.getScene().getWindow();

        // show the text mining analysis dialog in the new stage/window
        Stage secondary = new Stage();
        secondary.initOwner(w);
        secondary.setTitle("HPO text mining analysis");
        secondary.setScene(new Scene(hpoTextMining.getMainParent()));
        secondary.showAndWait();

        Set<PhenotypeTerm> approved = hpoTextMining.getApprovedTerms();

        if (miningTaskType.equals(PHENOPACKET)) {
            PhenopacketModel pmodel = (PhenopacketModel) this.model;
            Optional<LocalDate> bdateOpt = pmodel.getBirthdate();
            if (bdateOpt.isEmpty()) {
                PopUps.showInfoMessage("Error", "Cannot add phenotypes without initializing birthdate");
                return;
            }
            Period age = getAge(bdateOpt.get(), encounterDate);
            List<FenominalTerm> approvedTerms = approved.stream()
                    .map(pterm -> FenominalTerm.fromMainPhenotypeTermWithAge(pterm, age))
                    .sorted()
                    .collect(Collectors.toList());
            pmodel.addHpoFeatures(approvedTerms);
            int encountersSoFar = pmodel.casesMined();
            this.parseButton.setText(String.format("Mine encounter %d", encountersSoFar + 1));
        } else if (miningTaskType.equals(PHENOPACKET_BY_AGE)) {
            Period agePeriod = Period.parse(isoAge);
            List<FenominalTerm> approvedTerms = approved.stream()
                    .map(pterm -> FenominalTerm.fromMainPhenotypeTermWithIsoAge(pterm, agePeriod))
                    .sorted()
                    .collect(Collectors.toList());
            model.addHpoFeatures(approvedTerms);
            int encountersSoFar = model.casesMined();
            this.parseButton.setText(String.format("Mine encounter %d", encountersSoFar + 1));
        } else {
            PopUps.showInfoMessage("Error, mining task not implemented yet", "Error");
            return;
        }

        updateTable();
        // if we get here, we have data that could be output
        this.previwButton.setDisable(false);
        this.outputButton.setDisable(false);
        e.consume();
    }

    private void updateTable() {
        if (this.model == null) {
            LOGGER.error("Attempt to update table while model was null");
            return;
        }
        if (model.casesMined() > 0) {
            model.setModelDataItem("patients (n)", String.valueOf(model.casesMined()));
        }
        model.setModelDataItem("terms curated (n)", String.valueOf(model.getTermCount()));
        populateTableWithData(model.getModelData());
    }

    /**
     * @return Version of HPO being used for curation, corresponding to the data-version attribute in hp.json
     */
    private String getHpoVersion() {
        Ontology hpo = optionalResources.getOntology();
        if (hpo != null) {
            return hpo.getMetaInfo().getOrDefault("data-version", "n/a");
        } else {
            return "not initialized";
        }
    }


    /**
     * Set up parsing for a single individual over one or more time points with the goal of outputting a
     * GA4GH phenopacket with one or multiple time points
     */
    private void initPhenopacket() {
        this.parseButton.setDisable(false);
        this.parseButton.setText("Mine time point 1");
        this.miningTaskType = PHENOPACKET;
        Optional<PatientSexIdAndBirthdate> opt = BirthDatePickerDialog.showDatePickerDialogSIB();
        if (opt.isEmpty()) {
            PopUps.showInfoMessage("Error", "Could not retrieve id/sex/birthdate");
            return;
        }
        PatientSexIdAndBirthdate psidb = opt.get();
        String id = psidb.id();
        Sex sex = psidb.sex();
        LocalDate birthdate = psidb.birthdate();
        LOGGER.info("Retrieved id {} and sex {} and birthdate {}", id, sex, birthdate);
        this.model = new PhenopacketModel(id, sex);
        model.setModelDataItem(HPO_VERSION_KEY, getHpoVersion());
        model.setModelDataItem(PATIENT_ID_KEY, id);
        model.setModelDataItem(N_CURATED_KEY, "0");
        model.setBirthdate(birthdate);
        populateTableWithData(model.getModelData());
    }



    private void initPhenopacketWithManualAge() {
        this.parseButton.setDisable(false);
        Optional<PatientSexAndId> opt = BirthDatePickerDialog.showDatePickerDialogSI();
        if (opt.isEmpty()) {
            PopUps.showInfoMessage("Error", "Could not initialize Phenopacket");
            return;
        }
        PatientSexAndId psid = opt.get();
        this.parseButton.setText("Mine encounter 1");
        this.miningTaskType = PHENOPACKET_BY_AGE;
        this.model = new PhenopacketByAgeModel(psid.id(), psid.sex());
        model.setModelDataItem(HPO_VERSION_KEY, getHpoVersion());
        model.setModelDataItem(N_CURATED_KEY, "0");
        populateTableWithData(model.getModelData());
    }


    @FXML
    private void getStarted(ActionEvent e) {
        if (! cleanBeforeNewCase()) {
            return;
        }
        var caseReport = new CommandLinksDialog.CommandLinksButtonType("Case report", "Enter data about one individual, one time point", true);
        var phenopacketByBirthDate = new CommandLinksDialog.CommandLinksButtonType("Phenopacket", "Enter data about one individual, multiple time points", false);
        var cohortTogether = new CommandLinksDialog.CommandLinksButtonType("Cohort", "Enter data about cohort", false);
        var phenopacketByIso8601Age = new CommandLinksDialog.CommandLinksButtonType("Phenopacket (by age at encounter)", "Enter data about one individual, multiple ages", false);
        var cancel = new CommandLinksDialog.CommandLinksButtonType("Cancel", "Go back and do not delete current work", false);
        CommandLinksDialog dialog = new CommandLinksDialog(phenopacketByBirthDate, phenopacketByIso8601Age, caseReport, cohortTogether, cancel);
        dialog.setTitle("Get started");
        dialog.setHeaderText("Select type of curation");
        dialog.setContentText("Fenominal supports four types of HPO biocuration.");
        Optional<ButtonType> opt = dialog.showAndWait();
        if (opt.isPresent()) {
            ButtonType btype = opt.get();
            switch (btype.getText()) {
                case "Phenopacket":
                    initPhenopacket();
                    break;
                case "Phenopacket (by age at encounter)":
                    initPhenopacketWithManualAge();
                    break;
                case "Cancel":
            }
            // If we are here, then we are starting a new Phenopacket and
            // we should not offer the update option.
            this.updatePhenopacketButton.setDisable(true);
        }
        String biocurator = this.pgProperties.getProperty(BIOCURATOR_ID_PROPERTY);
        if (biocurator != null) {
            this.model.setModelDataItem(BIOCURATOR_ID_PROPERTY, biocurator);
        }
        this.questionnaireButtn.setDisable(false);
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
        if (shutdown()) {
            Platform.exit();
        }
        e.consume();
    }

    @FXML
    public void outputButtonPressed(ActionEvent actionEvent) {
        actionEvent.consume();
        String initialFilename = model.getInitialFileName();
        LOGGER.info("Saving data with initial file name {}", initialFilename);
        FileChooser fileChooser = new FileChooser();
        Stage stage = (Stage) this.outputButton.getScene().getWindow();
        fileChooser.setInitialFileName(initialFilename);
        File file = fileChooser.showSaveDialog(stage);
        if (file == null) {
            PopUps.showInfoMessage("Could not retrieve output file name, please try again.", "Error");
            return;
        }
        LOGGER.info("Retrieved file for saving: {}", file.getAbsolutePath());
        try (Writer writer = new BufferedWriter(new FileWriter(file))) {
            PhenoOutputter phenoOutputter;
            switch (this.miningTaskType) {
                case PHENOPACKET -> phenoOutputter = new PhenopacketJsonOutputter((PhenopacketModel) this.model);
                case PHENOPACKET_BY_AGE -> phenoOutputter = new PhenopacketByAgeJsonOutputter((PhenopacketByAgeModel) this.model);
                default -> phenoOutputter = new ErrorOutputter();
            }
            phenoOutputter.output(writer);
        } catch (IOException e) {
            PopUps.showInfoMessage("Could not write to file: " + e.getMessage(), "IO Error");
        }
        this.model.resetChanged(); // we have now saved all unsaved data if we get here.
        this.questionnaireButtn.setDisable(false);
    }

    @FXML
    public void previewOutput(ActionEvent e) {
        PhenoOutputter phenoOutputter;
        LOGGER.info("preview output");
        Writer writer = new StringWriter();
        phenoOutputter = switch (this.miningTaskType) {
            case PHENOPACKET -> new PhenopacketJsonOutputter((PhenopacketModel) this.model);
            case PHENOPACKET_BY_AGE -> new PhenopacketByAgeJsonOutputter((PhenopacketByAgeModel) this.model);
            default -> new ErrorOutputter();
        };
        try {
            phenoOutputter.output(writer);
            writer.close();
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
        }
        Text text1 = new Text(writer.toString());
        text1.setFill(Color.BLUE);
        text1.setFont(Font.font("Helvetica", FontPosture.REGULAR, 14));
        TextFlow textFlow = new TextFlow(text1);
        ScrollPane spane = new ScrollPane(textFlow);
        Stage stage = new Stage();
        Scene previewOutputScene = new Scene(spane);
        stage.setScene(previewOutputScene);
        stage.setHeight(750);
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
        if (this.model == null) {
            PopUps.showInfoMessage("Error", "Cannot invoke questionnaire before initializing case");
            return;
        }

        Ontology hpo = this.optionalResources.getOntology();
        PhenoQuestionnaire pq = PhenoQuestionnaire.development(hpo);
        QuestionnairePane qpane = new QuestionnairePane();
        qpane.setQuestionnaire(pq.getQuestions());
        int height = pq.getQuestions().size() * 80 + 100;
        Scene scene = new Scene(qpane, 1200, height);
        Stage secondary = new Stage();
        secondary.setTitle("PhenoQuestionnaire");
        secondary.setScene(scene);
        secondary.showAndWait();
        List<PhenoAnswer> answers = qpane.getAnswers();
        // Transform the PhenoAnswer objects into FenominalTerm objects to add them to our model
        List<FenominalTerm> fterms = new ArrayList<>();
        for (PhenoAnswer answer : answers) {
            if (answer.unknown()) {
                LOGGER.error("Unknown Phenoanswer passed to controller (should never happen");
                continue;
            }
            fterms.add(new FenominalTerm(answer.term(), answer.observed()));
            LOGGER.info("Adding fterm {}", answer.term().getName());
        }
        LOGGER.info("Adding HPO features from questionnaire, n={}", fterms.size());
        model.addHpoFeatures(fterms);


        updateTable();
        this.previwButton.setDisable(false);
        this.outputButton.setDisable(false);
    }


    public void openAboutDialog(ActionEvent e) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Fenominal");
        alert.setHeaderText(null);
        String fenomimalVersion = applicationProperties.getApplicationVersion();
        alert.setContentText(String.format("Version %s", fenomimalVersion));
        alert.showAndWait();
        e.consume();
    }

    /**
     * Loads a prexisting Phenopacket and populates the model
     *
     * @param phenopacketImp Importer object
     */
    private void loadPhenopacket(PhenopacketImporter phenopacketImp) {
        this.parseButton.setDisable(false);
        this.parseButton.setText("Mine time point 1");
        this.miningTaskType = PHENOPACKET;

        this.model = new PhenopacketModel(phenopacketImp);
        Optional<LocalDate> opt = BirthDatePickerDialog.showDatePickerDialogBirthDate(phenopacketImp);
        if (opt.isEmpty()) {
            PopUps.showInfoMessage("Error", "Could not load phenopacket");
            return;
        }
        LocalDate birthdate = opt.get();
        model.setBirthdate(birthdate);
        model.setModelDataItem(HPO_VERSION_KEY, getHpoVersion());
        model.setModelDataItem(PATIENT_ID_KEY, phenopacketImp.getId());
        populateTableWithData(model.getModelData());
    }

    /**
     * Loads a prexisting Phenopacket and populates the model
     *
     * @param phenopacketImp Importer object
     */
    private void loadPhenopacketWithManualAge(PhenopacketImporter phenopacketImp) {
        this.parseButton.setDisable(false);
        this.parseButton.setText("Mine time point 1");
        this.miningTaskType = PHENOPACKET;

        this.model = new PhenopacketByAgeModel(phenopacketImp);
        model.setModelDataItem(HPO_VERSION_KEY, getHpoVersion());
        model.setModelDataItem(PATIENT_ID_KEY, phenopacketImp.getId());
        populateTableWithData(model.getModelData());
    }

    public void updatePhenopacket(ActionEvent actionEvent) {
        var phenopacketByBirthDate = new CommandLinksDialog.CommandLinksButtonType("Phenopacket", "Enter age via brithdate/encounter date", false);
        var phenopacketByIso8601Age = new CommandLinksDialog.CommandLinksButtonType("Phenopacket (by age at encounter)", "Enter dage directly", false);
        var cancel = new CommandLinksDialog.CommandLinksButtonType("Cancel", "Cancel", false);
        CommandLinksDialog dialog = new CommandLinksDialog(phenopacketByBirthDate, phenopacketByIso8601Age, cancel);
        dialog.setTitle("Update Phenopacket");
        dialog.setHeaderText("Select type of curation");

        dialog.setContentText("Select a phenopacket file for updating.");
        Optional<ButtonType> opt = dialog.showAndWait();
        if (opt.isPresent()) {
            Optional<PhenopacketImporter> optpp = loadPhenopacketFromFile();
            if (optpp.isEmpty()) {
                PopUps.showInfoMessage("Error", "Could not load phenopacket file");
                return;
            }
            ButtonType btype = opt.get();
            switch (btype.getText()) {
                case "Phenopacket":
                    loadPhenopacket(optpp.get());
                    this.miningTaskType = PHENOPACKET;
                    break;
                case "Phenopacket (by age at encounter)":
                    loadPhenopacketWithManualAge(optpp.get());
                    this.miningTaskType = PHENOPACKET_BY_AGE;
                    break;
                case "Cancel":
                default:
                    return;
            }
        }
        String biocurator = this.pgProperties.getProperty(BIOCURATOR_ID_PROPERTY);
        if (biocurator != null) {
            this.model.setModelDataItem(BIOCURATOR_ID_PROPERTY, biocurator);
        }
        this.questionnaireButtn.setDisable(false);
        // We can only update a phenopacket once, so now disable the button
        this.updatePhenopacketButton.setDisable(true);
        actionEvent.consume();
    }


    private Optional<PhenopacketImporter> loadPhenopacketFromFile() {
        FileChooser fileChooser = new FileChooser();
        // limit to *.json
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json");
        fileChooser.getExtensionFilters().add(extFilter);
        Stage stage = (Stage) this.outputButton.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);
        if (file == null) {
            return Optional.empty();
        }
        Ontology ontology = optionalResources.getOntology();
        if (ontology == null) {
            PopUps.showInfoMessage("Error", "Cannot import Phenopacket before initialized HPO.");
            return Optional.empty();
        }
        PhenopacketImporter ppacket = PhenopacketImporter.fromJson(file, ontology);
        return Optional.of(ppacket);
    }

    public boolean cleanBeforeNewCase() {
        if (model == null) {
            return true;
        }
        if (model.isChanged()) {
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Warning - Unsaved Data");
            dialog.setHeaderText("Discard changes?");
            dialog.setContentText("Cancel revokes the new case request");
            DialogPane dialogPane = dialog.getDialogPane();
            dialogPane.getButtonTypes().addAll(ButtonType.YES, ButtonType.CANCEL);
            Optional<ButtonType> opt = dialog.showAndWait();
            if (opt.isEmpty()) return false;
            ButtonType btype = opt.get();
            if (btype.equals(ButtonType.CANCEL)) return false;
            return  (btype.equals(ButtonType.YES));
        }
        // if we get here, somethinbg probably went wrong, let's cancel the quit request
        return false;
    }

    public boolean shutdown() {
        if (model == null) {
            return true; // in this case, the use has not started anything and just wants out
        }
        if (model.isChanged()) {
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Warning - Unsaved Data");
            dialog.setHeaderText("Discard changes?");
            dialog.setContentText("Cancel revokes the exit request");
            DialogPane dialogPane = dialog.getDialogPane();
            dialogPane.getButtonTypes().addAll(ButtonType.YES, ButtonType.CANCEL);
            Optional<ButtonType> opt = dialog.showAndWait();
            if (opt.isEmpty()) return false;
            ButtonType btype = opt.get();
            if (btype.equals(ButtonType.CANCEL)) return false;
            return  (btype.equals(ButtonType.YES));
        }
        // if we get here, somethinbg probably went wrong, let's cancel the quit request
        return false;
    }
}
