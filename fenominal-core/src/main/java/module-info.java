module org.monarchinitiative.fenominal.core {
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.databind;
    requires org.slf4j;
    requires org.monarchinitiative.phenol.core;
    requires curie.util;
    requires org.yaml.snakeyaml;
    requires org.apache.commons.io;
    requires org.monarchinitiative.phenol.io;
    exports org.monarchinitiative.fenominal.core;
    exports org.monarchinitiative.fenominal.core.hpo;
    exports org.monarchinitiative.fenominal.core.corenlp;
    exports org.monarchinitiative.fenominal.core.kmer;
}