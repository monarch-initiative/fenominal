module fenominal.core {
    // TODO - it would be nice to think more about the API of this module to prevent having to export all packages
    exports org.monarchinitiative.fenominal.core;
    exports org.monarchinitiative.fenominal.core.corenlp;
    exports org.monarchinitiative.fenominal.core.textmapper;
    exports org.monarchinitiative.fenominal.core.except;

    requires stanford.corenlp;
    requires phenol.core;
    requires phenol.io; // TODO - remove as soon we get rid of the IO module here

}