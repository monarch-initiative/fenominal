module org.monarchinitiative.fenominal.core {
    requires org.monarchinitiative.phenol.core;

    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.databind;
    requires org.apache.commons.io;
    requires org.slf4j;

    exports org.monarchinitiative.fenominal.model;
    exports org.monarchinitiative.fenominal.core;
}