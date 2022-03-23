package org.monarchinitiative.fenominal.gui.hpotextminingwidget;


import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.*;

/**
 * This class acts as the controller of the main dialog window of the {@link HpoTextMining} analysis dialog. The dialog
 * window can be divided into these subparts:
 * <ul>
 * <li><b>ontology tree pane</b> - tree hierarchy of the ontology is presented here</li>
 * <li><b>text-mining pane</b> - place where user submits a query text that is mined for HPO terms. The results
 * of the analysis are then presented in the same pane</li>
 * <li><b>approved terms</b> - table with the approved terms</li>
 * </ul>
 * <p>
 * The terms which are inside the <em>approved terms</em> table are available by from {@link #getPhenotypeTerms()} method
 * as {@link PhenotypeTerm} instances.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.1.0
 * @since 0.1
 */
public class HpoTextMiningMain {

    /**
     * Ontology is presented here by {@link OntologyTree} controller.
     */
    @FXML
    public StackPane leftStackPane;

    /**
     * Content controlled by {@link Configure} and {@link Present} is being displayed here
     */
    @FXML
    private StackPane textMiningStackPane;


    /**
     * Temporary copy for storing already present PhenotypeTerms until the controller GUI elements are initialized by
     * the FXML loader. The content is moved into the TableView in {@link #initialize()} method.
     */
    private final Set<PhenotypeTerm> terms = new HashSet<>();


    /**
     * This table contains accepted {@link PhenotypeTerm}s.
     */
    @FXML
    private TableView<PhenotypeTerm> hpoTermsTableView;

    @FXML
    private TableColumn<PhenotypeTerm, String> hpoIdTableColumn;

    @FXML
    private TableColumn<PhenotypeTerm, String> hpoNameTableColumn;

    @FXML
    private TableColumn<PhenotypeTerm, String> observedTableColumn;

    @FXML
    private TableColumn<PhenotypeTerm, String> definitionTableColumn;


    /**
     * Create the controller.
     */
    public HpoTextMiningMain() {
        // no-op
    }

    void setTextMiningContent(Node content) {
        textMiningStackPane.getChildren().clear();
        textMiningStackPane.getChildren().add(content);
    }

    void setLeftStackPaneContent(Node content) {
        leftStackPane.getChildren().clear();
        leftStackPane.getChildren().add(content);
    }

    /**
     * Get set of approved {@link PhenotypeTerm}s.
     *
     * @return new {@link Set} containing the approved terms
     */
    Set<PhenotypeTerm> getPhenotypeTerms() {
        return new HashSet<>(hpoTermsTableView.getItems());
    }

    /**
     * Add a set of {@link PhenotypeTerm} which will be displayed in table at the bottom of the dialog.
     *
     * @param terms {@link Set} of {@link PhenotypeTerm}s
     */
    void addPhenotypeTerms(Set<PhenotypeTerm> terms) {
        for (PhenotypeTerm term : terms) {
            addPhenotypeTerm(term);
        }
    }

    void addPhenotypeTerm(PhenotypeTerm term) {
        if (hpoTermsTableView == null) { // make a temporary copy until processing of GUI elements by FXMLLoader
            terms.add(term);
        } else {
            if (!hpoTermsTableView.getItems().contains(term)) { // add the term if it is not already there
                hpoTermsTableView.getItems().add(term);
            }
        }
    }

    /**
     * Remove selected {@link PhenotypeTerm} from TableView.
     */
    @FXML
    void removeButtonAction() {
        hpoTermsTableView.getItems().removeAll(hpoTermsTableView.getSelectionModel().getSelectedItems());
    }

    /**
     * Initialize JavaFX components of {@link HpoTextMiningMain} controller.
     */
    public void initialize() {
        // initialize behaviour of columns of the TableView
        hpoIdTableColumn.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().getTerm().id().getValue()));
        hpoNameTableColumn.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().getTerm().getName()));
        observedTableColumn.setCellValueFactory(cdf -> new ReadOnlyStringWrapper((cdf.getValue().isPresent()) ? "YES" : "NOT"));
        definitionTableColumn.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().getTerm().getDefinition()));

        if (!terms.isEmpty()) {
            hpoTermsTableView.getItems().addAll(terms);
        }
    }

    @FXML
    public void removeTermButtonAction() {
        if (!hpoTermsTableView.getItems().isEmpty()) {
            List<PhenotypeTerm> toBeRemoved = new ArrayList<>();
            for (Integer index : hpoTermsTableView.getSelectionModel().getSelectedIndices()) {
                toBeRemoved.add(hpoTermsTableView.getItems().get(index));
            }
            hpoTermsTableView.getItems().removeAll(toBeRemoved);
        }
    }

    @FXML
    public void confirmAndClose() {
        Stage stage = (Stage) leftStackPane.getScene().getWindow();
        stage.close();
    }

    /**
     * Enum used by {@link Configure} and {@link Present} to signalize progres & status.
     */
    enum Signal {
        DONE,
        CANCELLED,
        FAILED
    }


}
