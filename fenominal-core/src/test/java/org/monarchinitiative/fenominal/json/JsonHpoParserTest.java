package org.monarchinitiative.fenominal.json;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;

public class JsonHpoParserTest {

    private final static File smallHpo = Paths.get("/Users/robinp/GIT/human-phenotype-ontology/hp.json").toFile();


    @Test
    public void testRead() {
        JsonHpoParser parser = new JsonHpoParser(smallHpo.getAbsolutePath());
    }

}
