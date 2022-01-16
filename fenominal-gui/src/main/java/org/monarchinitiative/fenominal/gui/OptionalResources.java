package org.monarchinitiative.fenominal.gui;


import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class OptionalResources {
    private static final Logger LOGGER = LoggerFactory.getLogger(OptionalResources.class);
    /**
     * Use this name to save HP.json file on the local filesystem.
     */
    public static final String DEFAULT_HPO_FILE_NAME = "hp.json";
    public static final String ONTOLOGY_PATH_PROPERTY = "hp.json.path";

    // default value does not harm here
    private final ObjectProperty<Ontology> ontology = new SimpleObjectProperty<>(this, "ontology");

    private final StringProperty biocurator = new SimpleStringProperty(this, "biocurator.id");

    public Ontology getOntology() {
        return ontology.get();
    }


    public void setOntology(Ontology ontology) {
        this.ontology.set(ontology);
    }


    public ObjectProperty<Ontology> ontologyProperty() {
        return ontology;
    }

    public void setBiocurator(String id) {
        biocurator.setValue(id);
    }

    public StringProperty biocuratorIdProperrty() { return biocurator; }


}
