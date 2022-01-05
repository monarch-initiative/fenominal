package org.monarchinitiative.fenominal.gui.hpotextminingwidget;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * This class is responsible for displaying the terms of performed text-mining analysis. <p>The controller accepts
 * response from the server performing text-mining analysis in JSON format and the analyzed text. The analyzed text
 * with highlighted term-containing regions is presented to the user. Tooltips containing the HPO term id and name are
 * also created for the highlighted regions. After clicking on the highlighted region, corresponding term is selected
 * in the ontology TreeView (left part of the main window).
 * <p>
 * Identified <em>YES</em> and <em>NOT</em> HPO terms are displayed on the right side of the screen as a set of
 * checkboxes. The user/biocurator is supposed to review the analyzed text and select those checkboxes that have been
 * identified correctly.
 * <p>
 * Selected terms must be approved with <em>Add selected terms</em> button in order to add them into the model.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @author <a href="mailto:aaron.zhangl@jax.org">Aaron Zhang</a>
 * @version 0.1.0
 * @since 0.1
 */
public class Present {

    private static final Logger LOGGER = LoggerFactory.getLogger(Present.class);

    /**
     * Header of html defining CSS & JavaScript for the presented text. CSS defines style for tooltips and
     * highlighted text. JavaScript code will allow focus on HPO term in the ontology treeview after clicking on the
     * highlighted text.
     */
    private static final String HTML_HEAD = "<html><head>" +
            "<style> .tooltip { position: relative; display: inline-block; border-bottom: 1px dotted black; }" +
            ".tooltip .tooltiptext { visibility: hidden; width: 230px; background-color: #555; color: #fff; " +
            "text-align: left;" +
            " border-radius: 6px; padding: 5px 0; position: absolute; z-index: 1; bottom: 125%; left: 50%; margin-left: -60px;" +
            " opacity: 0; transition: opacity 1s; }" +
            ".tooltip .tooltiptext::after { content: \"\"; position: absolute; top: 100%; left: 50%; margin-left: -5px;" +
            " border-width: 5px; border-style: solid; border-color: #555 transparent transparent transparent; }" +
            ".tooltip:hover .tooltiptext { visibility: visible; opacity: 1;}" +
            "</style>" +
            "<script>function focusOnTermJS(obj) {javafx_bridge.focusToTerm(obj);}</script>" +
            "</head>";

    private static final String HTML_BODY_BEGIN = "<body><h2>HPO text-mining analysis terms:</h2><p>";

    private static final String HTML_BODY_END = "</p></body></html>";

    /**
     * Html template for highlighting the text based on which a HPO term has been identified. Contains three
     * placeholders: <ol>
     * <li>HPO term ID (param for javascript, it will be used to focus on HPO term in the ontology tree)</li>
     * <li>part of the query text based on which the HPO term has been identified</li>
     * <li>tooltip text</li> </ol>
     * The initial space is intentional, it prevents lack of space between words with series of hits.
     */
    private static final String HIGHLIGHTED_TEMPLATE = " " +
            "<span class=\"tooltip\" style=\"color:red;\" onclick=\"focusOnTermJS('%s')\">%s" +
            "<span class=\"tooltiptext\">%s</span></span>";

    /**
     * Template for tooltips which appear when cursor hovers over highlighted terms.
     */
    private static final String TOOLTIP_TEMPLATE = "%s\n%s";

    private final Consumer<TermId> focusToTermHook;

    private final Consumer<Main.Signal> signal;

    /**
     * The GUI element responsible for presentation of analyzed text with highlighted regions.
     */
    @FXML
    private WebView webView;

    private WebEngine webEngine;

    @FXML
    private Button addTermsButton;

    @FXML
    private Button cancelButton;

    /**
     * Box on the right side of the screen where "YES" Terms will be added.
     */
    @FXML
    private VBox yesTermsVBox;

    /**
     * Box on the right side of the screen where "NOT" Terms will be added.
     */
    @FXML
    private VBox notTermsVBox;

    @FXML
    private ScrollPane notTermScrollPane;

    @FXML
    private ScrollPane yesTermScrollPane;

    /**
     * Observable Checkboxes corresponding to identified <em>YES</em> HPO terms.
     */
    private ObservableSet<PhenotypeTerm> yesTerms = FXCollections.observableSet();

    /**
     * Observable Checkboxes corresponding to identified <em>NOT</em> HPO terms.
     */
    private ObservableSet<PhenotypeTerm> notTerms = FXCollections.observableSet();

    /**
     * Tracks the selection state of all terms. Note: use Term rather than PhenotypeTerm as the latter could mutate
     * its negation term.
     */
    private ObservableSet<Term> checkBoxesState = FXCollections.observableSet();


    /**
     * @param signal          {@link Consumer} of {@link Main.Signal}
     *                        that will notify the upstream controller about status of the analysis
     * @param focusToTermHook {@link Consumer} that will accept {@link TermId} in order to show appropriate {@link Term} in ontology
     *                        tree view
     */
    Present(Consumer<Main.Signal> signal, Consumer<TermId> focusToTermHook) {
        this.signal = signal;
        this.focusToTermHook = focusToTermHook;
    }

    /**
     * Create checkbox on the fly applying desired style, padding, etc. The <code>term</code> is added to {@link CheckBox}
     * so that it can be later retrieved using {@link CheckBox#getUserData()}.
     *
     * @param term - {@link Term} to be represented by a Checkbox
     * @return created {@link CheckBox} instance
     */
    private static CheckBox checkBoxFactory(PhenotypeTerm term) {
        CheckBox cb = new CheckBox(term.getTerm().getName());
        cb.setPadding(new Insets(5));
        cb.setUserData(term);
        return cb;
    }

    /**
     * Collection of {@link PhenotypeTerm}s submitted in
     * {@link #setResults(Collection, String)} methods may contain the same HPO terms present at multiple sites of query
     * text (if the same term is mentioned in multiple sites of query text).
     * <p>
     * We still want to show only one CheckBox per term.
     * <p>
     * Here we get the {@link PhenotypeTerm}s that represent
     * unique {@link Term}.
     *
     * @param terms {@link Collection} of {@link PhenotypeTerm}
     *              submitted to {@link #setResults(Collection, String)} method
     * @return {@link List} of {@link PhenotypeTerm} that
     * represent unique {@link Term}s.
     */
    private static List<PhenotypeTerm> deduplicate(Collection<PhenotypeTerm> terms) {
        Set<String> ids = new HashSet<>();
        List<PhenotypeTerm> deduplicated = new ArrayList<>();
        for (PhenotypeTerm term : terms) {
            if (!ids.contains(term.getTerm().getId().getId())) {
                deduplicated.add(term);
            }
            ids.add(term.getTerm().getId().getId());
        }
        return deduplicated;
    }

    /**
     * Similar to above but this one works for terms from SciGraph server
     *
     * @author Aaron Zhang
     */
    private String colorizeHTML4ciGraph(Collection<PhenotypeTerm> terms, String query) {
        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append(HTML_HEAD);
        htmlBuilder.append(HTML_BODY_BEGIN);

        // sort to process minedText sequentially.
        final List<PhenotypeTerm> sortedByBegin = terms.stream()
                .sorted(Comparator.comparing(PhenotypeTerm::getBegin))
                .collect(Collectors.toList());

        int offset = 0;
        for (PhenotypeTerm term : sortedByBegin) {
            int start = Math.max(term.getBegin(), offset);
            htmlBuilder.append(query, offset, start); // unhighlighted text
            //start = Math.max(offset + 1, result.getStart());
            //Term id is an information such as "HP:0000822"
            start = Math.min(start,term.getBegin());
            htmlBuilder.append(
                    // highlighted text
                    String.format(HIGHLIGHTED_TEMPLATE,
                            term.getTerm().getId().getValue(),
                            query.substring(start, term.getEnd()),

                            // tooltip text -> HPO id & label
                            String.format(TOOLTIP_TEMPLATE, term.getTerm().getId().getValue(), term.getTerm().getName())));

            offset = term.getEnd();
        }

        // process last part of mined text, if there is any
        htmlBuilder.append(query.substring(offset));
        htmlBuilder.append(HTML_BODY_END);
        // get rid of double spaces
        return htmlBuilder.toString().replaceAll("\\s{2,}", " ").trim();
    }

    /**
     * End of analysis. Add approved terms into {@link Main}'s <code>hpoTermsTableView</code> and display configure
     * Dialog to allow next round of text-mining analysis.
     */
    @FXML
    void addTermsButtonAction() {
        signal.accept(Main.Signal.DONE);
    }

    /**
     * After hitting {@link Present#cancelButton} the analysis is ended and a new {@link Configure} dialog is presented
     * to the user.
     */
    @FXML
    void cancelButtonAction() {
        signal.accept(Main.Signal.CANCELLED);
    }

    /**
     * {@inheritDoc}
     */
    public void initialize() {
        webEngine = webView.getEngine();
        // register JavaBridge object in the JavaScript engine of the webEngine
        webEngine.getLoadWorker().stateProperty().addListener((ov, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                JSObject win = (JSObject) webEngine.executeScript("window");
                win.setMember("javafx_bridge", new JavaBridge());
                // redirect JavaScript console.LOGGER() to sysout defined in the JavaBridge
                webEngine.executeScript("console.log = function(message) {javafx_bridge.log(message);};");
            }
        });

        //when yes terms are updated, re-create checkboxes for all terms
        yesTerms.addListener(new SetChangeListener<PhenotypeTerm>() {
            @Override
            public void onChanged(Change<? extends PhenotypeTerm> change) {
                yesTermsVBox.getChildren().clear();
                change.getSet().stream().sorted(Comparator.comparing(a -> a.getTerm().getName()))
                        .map(phenotype -> checkBoxFactory(phenotype)).forEach(yesTermsVBox.getChildren()::add);

            }
        });

        //same as above
        notTerms.addListener(new SetChangeListener<PhenotypeTerm>() {
            @Override
            public void onChanged(Change<? extends PhenotypeTerm> change) {
                notTermsVBox.getChildren().clear();
                change.getSet().stream().sorted(Comparator.comparing(a -> a.getTerm().getName()))
                        .map(phenotype -> checkBoxFactory(phenotype)).forEach(notTermsVBox.getChildren()::add);
            }
        });

        //The listener listens to checkbox list changes. It adds drag support to every checkbox.
        ListChangeListener<Node> changeListener = new ListChangeListener<Node>() {
            @Override
            public void onChanged(Change<? extends Node> c) {
                while (c.next()) {
                    if (c.wasAdded()) {
                        c.getAddedSubList().forEach(node -> {
                            CheckBox checkBox = (CheckBox) node;
                            //add drag detected listener
                            checkBox.setOnDragDetected(event -> {
                                Dragboard db = checkBox.startDragAndDrop(TransferMode.ANY);
                                ClipboardContent draggedTerm = new ClipboardContent();
                                draggedTerm.putString(checkBox.getText());
                                LOGGER.debug("dragged item: " + checkBox.getText());
                                db.setContent(draggedTerm);
                                event.consume();
                            });

                            //drag is done.
                            //nothing else is needed to do as the term is already removed when drag is dropped
                            //(see below)--it is easier to handle over there than doing it here
                            checkBox.setOnDragDone(event -> {
                                if (event.getTransferMode() == TransferMode.MOVE) {
                                    LOGGER.debug("drag and drop completed");
                                }
                                event.consume();
                            });

                            if (checkBoxesState.contains(((PhenotypeTerm) checkBox.getUserData()).getTerm())) {
                                checkBox.setSelected(true);
                            } else {
                                checkBox.setSelected(false);
                            }

                            checkBox.selectedProperty().addListener((selected, oldvalue, newvalue) -> {
                                if (newvalue) {
                                    checkBoxesState.add(((PhenotypeTerm) checkBox.getUserData()).getTerm());
                                } else {
                                    checkBoxesState.remove(((PhenotypeTerm) checkBox.getUserData()).getTerm());
                                }
                            });
                        });
                    }
                }

            }
        };

        //add drag listeners to all checkboxes for yes terms
        yesTermsVBox.getChildren().addListener(changeListener);
        //add drag listeners to all checkboxes for not terms
        notTermsVBox.getChildren().addListener(changeListener);

        //add drop listeners to the negated term list
        notTermScrollPane.setOnDragEntered(event -> {
            notTermScrollPane.setBackground(new Background(new BackgroundFill(Color.BLUE, null, null)));
            event.consume();
        });

        notTermScrollPane.setOnDragExited(event -> {
            notTermScrollPane.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
            event.consume();
        });

        notTermScrollPane.setOnDragOver(event -> {
            Dragboard dragboard = event.getDragboard();
            if (dragboard.hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        notTermScrollPane.setOnDragDropped(event -> {
            Dragboard dragboard = event.getDragboard();
            if (dragboard.hasString()) {
                String dragged = dragboard.getString();
                Optional<PhenotypeTerm> dragged_term = yesTerms.stream()
                        .filter(t -> t.getTerm().getName().equals(dragged)).findFirst();
                //remove from the yesTerms
                dragged_term.ifPresent(yesTerms::remove);
                //change to no term and add to noTerms
//                dragged_term.ifPresent(phenotypeTerm -> phenotypeTerm.setIsPresent(false));
                dragged_term.ifPresent(phenotypeTerm -> notTerms.add(new PhenotypeTerm(phenotypeTerm, false)));
                //notice source that drop is completed
                event.setDropCompleted(true);
            }
            event.consume();
        });


        //add drop listeners to the yes term list
        yesTermScrollPane.setOnDragEntered(event -> {
            yesTermScrollPane.setBackground(new Background(new BackgroundFill(Color.BLUE, null, null)));
            event.consume();
        });

        yesTermScrollPane.setOnDragExited(event -> {
            yesTermScrollPane.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
            event.consume();
        });

        yesTermScrollPane.setOnDragOver(event -> {
            Dragboard dragboard = event.getDragboard();
            if (dragboard.hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        yesTermScrollPane.setOnDragDropped(event -> {
            Dragboard dragboard = event.getDragboard();
            if (dragboard.hasString()) {
                String dragged = dragboard.getString();
                Optional<PhenotypeTerm> dragged_term = notTerms.stream()
                        .filter(t -> t.getTerm().getName().equals(dragged)).findFirst();
                //remove from the yesTerms
                dragged_term.ifPresent(notTerms::remove);
                //change to no term and add to noTerms
//                dragged_term.ifPresent(phenotypeTerm -> phenotypeTerm.setIsPresent(true));
                dragged_term.ifPresent(phenotypeTerm -> yesTerms.add(new PhenotypeTerm(phenotypeTerm, true)));
                //notice source that drop is completed
                event.setDropCompleted(true);
            }
            event.consume();
        });

    }

    /**
     * The data that are about to be presented are set here. The String with JSON terms are coming from the
     * text-mining analysis performing server while the mined text is the text submitted by the user in Configure Dialog
     * (controlled by {@link Configure}).
     *
     * @param terms String in JSON format containing the result of text-mining analysis.
     * @param query String with the query text submitted by the user.
     */
    void setResults(Collection<PhenotypeTerm> terms, String query) {
        yesTerms.clear();
        notTerms.clear();

        List<PhenotypeTerm> termList = deduplicate(terms);
        termList.sort(Comparator.comparing(t -> t.getTerm().getName()));

        termList.stream().filter(t -> t.isPresent()).forEach(yesTerms::add);
        termList.stream().filter(t -> !t.isPresent()).forEach(notTerms::add);

        String html = colorizeHTML4ciGraph(termList, query);
        webEngine.loadContent(html);
    }

    /**
     * Return the final set of <em>YES</em> & <em>NOT</em> {@link PhenotypeTerm} objects which have been approved by
     * curator by ticking the checkbox.
     *
     * @return {@link Set} of approved {@link PhenotypeTerm}s.
     */
    Set<PhenotypeTerm> getApprovedTerms() {

        List<CheckBox> boxes = new ArrayList<>();
        for (Node child : yesTermsVBox.getChildren()) {
            CheckBox b = ((CheckBox) child);
            boxes.add(b);
        }

        for (Node child : notTermsVBox.getChildren()) {
            CheckBox b = ((CheckBox) child);
            boxes.add(b);
        }

        return boxes.stream()
                .filter(CheckBox::isSelected)
                .map(cb -> ((PhenotypeTerm) cb.getUserData()))
                .collect(Collectors.toSet());
    }


    /**
     * This class is the bridge between JavaScript run in the {@link #webView} and Java code.
     */
    public class JavaBridge {

        public void log(String message) {
            LOGGER.info(message);
        }


        /**
         * @param termId String like HP:1234567
         */
        public void focusToTerm(String termId) {
            LOGGER.debug("Focusing on term with ID {}", termId);
            TermId id = TermId.of(termId);
            focusToTermHook.accept(id);
        }
    }
}
