package org.monarchinitiative.fenominal.gui.hpotextminingwidget;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.util.Callback;
import org.monarchinitiative.fenominal.core.MinedTerm;
import org.monarchinitiative.fenominal.core.TermMiner;
import org.monarchinitiative.phenol.base.PhenolRuntimeException;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Driver class/API for JavaFX presentation of HPO text mining.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @author <a href="mailto:aaron.zhangl@jax.org">Aaron Zhang</a>
 */

public class HpoTextMining {

    private static final Logger LOGGER = LoggerFactory.getLogger(HpoTextMining.class);

    @Autowired
    Parent mainParent;

    @Autowired
    Node configureAnchorPane;

    @Autowired
    Node presentVBox;

    @Autowired
    Node ontologyTreeResourceNode;

    // ---------------------------------- CONTROLLERS and PARENTS ------------------------------------------------------

    /**
     * Main controller, part of the API.
     */
    private final HpoTextMiningMain main;

    /**
     * The query text is submitted here.
     */
    private Configure configure;



    /**
     * Results of text mining is presented here.
     */
    private Present present;



    /**
     * Ontology is displayed in this controller as a tree.
     */
    private OntologyTree ontologyTree;

    /**
     * @param ontology        {@link Ontology} to use for text mining
     * @param miner           {@link TermMiner} to use for HPO text mining
     * @param executorService {@link ExecutorService} to use for asynchronous tasks
     */
    public HpoTextMining(Ontology ontology,
                          TermMiner miner,
                          ExecutorService executorService) {
        main = new HpoTextMiningMain();
        // Set up "Configure" part of the screen
        Consumer<HpoTextMiningMain.Signal> configureSignal = signal -> {
            switch (signal) {
                case DONE -> {
                    Set<PhenotypeTerm> phenotypeTerms = configure.getTerms().stream()
                            .map(minedTermToPhenotypeTerm(ontology))
                            .filter(Objects::nonNull)
                            .collect(Collectors.toSet());
                    present.setResults(phenotypeTerms, configure.getQuery());
                    main.setTextMiningContent(presentVBox);
                }
                case FAILED -> LOGGER.warn("Sorry, text mining analysis failed."); // TODO - improve cancellation & failed handling
                case CANCELLED -> LOGGER.warn("Text mining analysis cancelled");
            }
        };
        configure = new Configure(miner, executorService, configureSignal);

        // Set up "Present" part of the screen
        Consumer<HpoTextMiningMain.Signal> presentSignal = signal -> {
            switch (signal) {
                case DONE -> {
                    main.addPhenotypeTerms(present.getApprovedTerms());
                    main.setTextMiningContent(configureAnchorPane);
                }
                case FAILED -> LOGGER.warn("Sorry, text mining analysis failed."); // TODO - improve cancellation & failed handling);
                case CANCELLED -> LOGGER.warn("Text mining analysis cancelled");
                default -> LOGGER.warn("Unknown option '{}'", signal);
            }
        };
        this.present = new Present(presentSignal, termId -> ontologyTree.focusOnTerm(ontology.getTermMap().get(termId)));

        // Set up "OntologyTree" part of the screen
        Consumer<PhenotypeTerm> addHook = (main::addPhenotypeTerm);
        this.ontologyTree = new OntologyTree(ontology, addHook);

        // Simple controller factory treating controller created above as singletons.
        Callback<Class<?>, Object> controllerFactory = clazz -> {
            if (clazz.equals(HpoTextMiningMain.class)) {
                return main;
            } else if (clazz.equals(Configure.class)) {
                return configure;
            } else if (clazz.equals(Present.class)) {
                return present;
            } else if (clazz.equals(OntologyTree.class)) {
                return ontologyTree;
            } else {
                LOGGER.warn("Unknown class '{}' requested", clazz);
                return null;
            }
        };

//
        try {

            URL url = HpoTextMining.class.getResource("/fxml/HpoTextMiningMain.fxml");
            FXMLLoader mainLoader = new FXMLLoader(url);
            mainLoader.setClassLoader(HpoTextMining.class.getClassLoader());
            File f = new File(url.getFile());
            LOGGER.info("Resource for HpoTextMiningMain.fxml: {} - exists? {}", url, f.isFile());
            mainLoader.setControllerFactory(controllerFactory);
            mainParent = mainLoader.load();

            url = HpoTextMining.class.getResource("/fxml/Configure.fxml");
            f = new File(url.getFile());
            LOGGER.info("Resource for Configure.fxml: {} - exists? {}", url, f.isFile());
            FXMLLoader configureLoader = new FXMLLoader(url);
            configureLoader.setClassLoader(HpoTextMining.class.getClassLoader());
            configureLoader.setControllerFactory(controllerFactory);
            configureAnchorPane = configureLoader.load();
            main.setTextMiningContent(configureAnchorPane);

            url = HpoTextMining.class.getResource("/fxml/Present.fxml");
            f = new File(url.getFile());
            LOGGER.info("Resource for Present.fxml: {} - exists? {}", url, f.isFile());
            FXMLLoader presentLoader = new FXMLLoader(url);
            presentLoader.setClassLoader(HpoTextMining.class.getClassLoader());
            presentLoader.setControllerFactory(controllerFactory);
            presentVBox = presentLoader.load();

            url = HpoTextMining.class.getResource("/fxml/OntologyTree.fxml");
            f = new File(url.getFile());
            LOGGER.info("Resource for OntologyTree.fxml: {} - exists? {}", url, f.isFile());
            FXMLLoader ontologyTreeLoader = new FXMLLoader(url);
            ontologyTreeLoader.setClassLoader(HpoTextMining.class.getClassLoader());
            ontologyTreeLoader.setControllerFactory(controllerFactory);
            main.setLeftStackPaneContent(ontologyTreeLoader.load());
        } catch (NullPointerException npe) {
            LOGGER.error("Could not dereference URL for controllers: {}", npe.getMessage());
        } catch (IOException e) {
            LOGGER.error("Could not load HPO Textminign widget: {}", e.getMessage());
            e.printStackTrace();
        }

    }

    /**
     * @param ontology {@link Ontology} to use for creating {@link Term} from the String representation of termId
     * @return {@link Function} for mapping {@link MinedTerm} to {@link PhenotypeTerm}. The function returns <code>null</code>
     * if the String representation of term ID is invalid or if there is not matching {@link Term} in the <code>ontology</code>
     */
    private static Function<MinedTerm, PhenotypeTerm> minedTermToPhenotypeTerm(Ontology ontology) {
        return mt -> {
            TermId termId = TermId.of(mt.getTermId());
            if (!termId.getValue().startsWith("HP")) { // we are only working with HPO
                return null;
            }
            Term term = ontology.getTermMap().get(termId);
            if (term == null) {
                LOGGER.warn("There is not a term with id '{}' in the currently used ontology", termId.getValue());
                return null;
            } else {
                return new PhenotypeTerm(term, mt);
            }
        };
    }

    /**
     * @return a new builder for {@link HpoTextMining} analysis
     */
    public static HpoTextMiningBuilder builder() {
        return new HpoTextMiningBuilder();
    }

    /**
     * @return {@link Parent} with all the GUI elements of the HpoTextMining widget
     */
    public Parent getMainParent() {
        return mainParent;
    }


    // -----------------------------------------------------------------------------------------------------------------

    /**
     * @return {@link Set} of {@link PhenotypeTerm}s
     * approved by the user
     */
    public Set<PhenotypeTerm> getApprovedTerms() {
        return main.getPhenotypeTerms();
    }

    public static final class HpoTextMiningBuilder {

        private Ontology ontology;

        private URL biolarkServerUrl, sciGraphServerUrl;

        private TermMiner miner;

        private ExecutorService executorService;


        private HpoTextMiningBuilder() {
            // no-op
        }


        /**
         * @param ontology {@link Ontology} to work with (mandatory)
         * @return this {@link HpoTextMiningBuilder} instance
         */
        public HpoTextMiningBuilder withOntology(Ontology ontology) {
            this.ontology = ontology;
            return this;
        }


        /**
         * @param miner {@link TermMiner} to use for the text mining analysis. The miner has precedence over any URL
         *              (SciGraph, Biolark, etc..)
         * @return this {@link HpoTextMiningBuilder} instance
         */
        public HpoTextMiningBuilder withTermMiner(TermMiner miner) {
            this.miner = miner;
            return this;
        }

        /**
         * @param executorService {@link ExecutorService} to be used for asynchronous tasks (optional)
         * @return this {@link HpoTextMiningBuilder} instance
         */
        public HpoTextMiningBuilder withExecutorService(ExecutorService executorService) {
            this.executorService = executorService;
            return this;
        }

        /**
         * @param terms {@link Set} of terms to display in the widget from the beginning
         * @return this {@link HpoTextMiningBuilder} instance
         */
        public HpoTextMiningBuilder withPhenotypeTerms(Set<PhenotypeTerm> terms) {
            throw new UnsupportedOperationException("Cannot add terms, (TODO)");
        }

        /**
         * @return a new {@link HpoTextMining} instance
         */
        public HpoTextMining build() {
            TermMiner usedMiner;
            if (this.miner != null) {
                usedMiner = miner;
            } else {
                throw new PhenolRuntimeException("Could not get miner for HpoTextMining");
            }

            Objects.requireNonNull(ontology, "Ontology must not be null");

            if (executorService == null) {
                executorService = Executors.newSingleThreadExecutor();
            }

            return new HpoTextMining(ontology, usedMiner, executorService);
        }
    }
}
