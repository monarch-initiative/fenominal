module org.monarchinitiative.fenominal.cli {
    requires org.monarchinitiative.fenominal.core;
    requires org.monarchinitiative.phenol.core;
    requires org.monarchinitiative.phenol.io;
    requires org.monarchinitiative.biodownload;
    requires info.picocli;
    requires org.slf4j;

    exports org.monarchinitiative.fenominal.cli.cmd to info.picocli;
    opens org.monarchinitiative.fenominal.cli.cmd to info.picocli;
}