# Attic

Texts that may have lost their meaning for now, but may become useful one day.

---

### Parking the module
module fenominal.core {
// TODO - it would be nice to think more about the API of this module to prevent having to export all packages
exports org.monarchinitiative.fenominal.core;
exports org.monarchinitiative.fenominal.core.corenlp;
exports org.monarchinitiative.fenominal.core.textmapper;
exports org.monarchinitiative.fenominal.core.except;
exports org.monarchinitiative.fenominal.json;

    requires phenol.core;
    //requires static stanford.corenlp;
    requires static org.slf4j;
    requires static com.google.common;
    requires static curie.util;
    requires static org.yaml.snakeyaml;
    //requires static com.fasterxml.jackson.annotation;
    //requires static com.fasterxml.jackson.databind;
    //requires com.fasterxml.jackson.databind; // TODO - remove as soon we get rid of the IO module here

}



/// cli
module-info.java

module fenominal.cli {
requires fenominal.core;

// requires org.monarchinitiative.phenol.phenol.core;
requires info.picocli;
requires org.apache.commons.net;

    requires org.slf4j;
}

/// gui

module fenominal.gui {
requires fenominal.core;
requires hpotextmining.gui;

    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    requires spring.context;
    requires spring.boot.autoconfigure;
    requires spring.beans;
    requires spring.core;
    requires spring.boot;

    requires lucene.sandbox;
}