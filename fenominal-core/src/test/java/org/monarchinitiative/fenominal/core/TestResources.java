package org.monarchinitiative.fenominal.core;

import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.ontology.data.Ontology;

import java.nio.file.Path;

public class TestResources {

    public static final Path TEST_BASE = Path.of("src/test/resources");
    private static final Path HPO_PATH = TEST_BASE.resolve("hpo").resolve("hp.json");
    private static final Path SMALL_HPO_PATH = TEST_BASE.resolve("hpo").resolve("hp_head.json");
    private static volatile Ontology HPO;
    private static volatile Ontology SMALL_HPO;

    public static Ontology hpo() {
        if (HPO == null) {
            synchronized (TestBase.class) {
                if (HPO == null)
                    HPO = OntologyLoader.loadOntology(HPO_PATH.toFile());
            }
        }
        return HPO;
    }

    public static Ontology smallHpo() {
        if (SMALL_HPO == null) {
            synchronized (TestBase.class) {
                if (SMALL_HPO == null)
                    SMALL_HPO = OntologyLoader.loadOntology(SMALL_HPO_PATH.toFile());
            }
        }
        return SMALL_HPO;
    }

    private TestResources(){}
}
