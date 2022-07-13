module org.monarchinitiative.fenominal.cli {
    requires info.picocli;
    requires org.monarchinitiative.fenominal.core;
    requires org.monarchinitiative.phenol.core;
    requires org.monarchinitiative.biodownload;
    requires org.slf4j;
    requires org.monarchinitiative.phenol.io;

    exports org.monarchinitiative.fenominal.cli.cmd to info.picocli;
    opens org.monarchinitiative.fenominal.cli.cmd to info.picocli;
}