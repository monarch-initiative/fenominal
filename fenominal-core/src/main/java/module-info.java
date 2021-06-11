module fenominal.core {
    // TODO - it would be nice to think more about the API of this module to prevent having to export all packages
    exports org.monarchinitiative.fenominal.core;
    exports org.monarchinitiative.fenominal.core.corenlp;
    exports org.monarchinitiative.fenominal.core.textmapper;
    exports org.monarchinitiative.fenominal.core.except;
    exports org.monarchinitiative.fenominal.json;

    requires stanford.corenlp;
    requires phenol.core;
    requires org.slf4j;
    requires com.google.common;
    requires curie.util;
    //requires com.fasterxml.jackson.databind;
    //requires jackson.core;
    requires com.fasterxml.jackson.annotation;
    requires org.yaml.snakeyaml;
    requires com.fasterxml.jackson.databind; // TODO - remove as soon we get rid of the IO module here

}