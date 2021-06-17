package org.monarchinitiative.fenominal.gui.config;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.monarchinitiative.fenominal.json.JsonHpoParser;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class OptionalResources {
    private static final Logger LOGGER = LoggerFactory.getLogger(OptionalResources.class);

    /**
     * Use this name to save HP.obo file on the local filesystem.
     */
    public static final String DEFAULT_HPO_FILE_NAME = "hp.json";

    private File ontologyPath;

    private final BooleanBinding hpoIsMissing;

    private final ObjectProperty<File> hpoFileOP = new SimpleObjectProperty<>(this, "diseaseCaseDir");

    public ObjectProperty<File> hpoFileProperty() {
        return hpoFileOP;
    }

    // default value does not harm here
    private final StringProperty biocuratorId = new SimpleStringProperty(this, "biocuratorId", "");

    private final ObjectProperty<Ontology> ontology = new SimpleObjectProperty<>(this, "ontology");

    public OptionalResources() {
        this.hpoIsMissing = Bindings.createBooleanBinding(() -> getOntologyPath() != null && getOntology() != null,
                hpoFileProperty());
    }

    public static Ontology deserializeOntology(File ontologyPath) {
        return JsonHpoParser.loadOntology(ontologyPath.getAbsolutePath());
    }





    public File getOntologyPath() {
        return ontologyPath;
    }


    public void setOntologyPath(File ontologyPath) {
        this.ontologyPath = ontologyPath;
    }
    public Ontology getOntology() {
        return ontology.get();
    }


    public void setOntology(Ontology ontology) {
        this.ontology.set(ontology);
    }


    public ObjectProperty<Ontology> ontologyProperty() {
        return ontology;
    }


}
