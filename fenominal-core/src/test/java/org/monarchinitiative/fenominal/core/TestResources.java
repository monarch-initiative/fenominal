package org.monarchinitiative.fenominal.core;

import org.monarchinitiative.phenol.io.MinimalOntologyLoader;
import org.monarchinitiative.phenol.ontology.data.MinimalOntology;

import java.nio.file.Path;

public class TestResources {

    public static final Path TEST_BASE = Path.of("src/test/resources");
    private static final Path HPO_PATH = TEST_BASE.resolve("hpo").resolve("hp.json");
    private static final Path SMALL_HPO_PATH = TEST_BASE.resolve("hpo").resolve("hp_head.json");
    private static volatile MinimalOntology HPO;
    private static volatile MinimalOntology SMALL_HPO;

    public static MinimalOntology hpo() {
        if (HPO == null) {
            synchronized (TestBase.class) {
                if (HPO == null)
                    HPO = MinimalOntologyLoader.loadOntology(HPO_PATH.toFile());
            }
        }
        return HPO;
    }

    public static MinimalOntology smallHpo() {
        if (SMALL_HPO == null) {
            synchronized (TestBase.class) {
                if (SMALL_HPO == null)
                    SMALL_HPO = MinimalOntologyLoader.loadOntology(SMALL_HPO_PATH.toFile());
            }
        }
        return SMALL_HPO;
    }

    private TestResources(){}
}
