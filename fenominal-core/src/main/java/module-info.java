module org.monarchinitiative.fenominal.core {
    // Ontology is in the TermMiner API (static methods).
    requires transitive org.monarchinitiative.phenol.core;

    requires org.slf4j;

    exports org.monarchinitiative.fenominal.model;
    exports org.monarchinitiative.fenominal.core;
}