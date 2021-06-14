package org.monarchinitiative.fenominal.json;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;

public class JsonHpoParserTest {
    private final static File smallHpo = Paths.get("src/test/resources/hpo/hp.small.json").toFile();

    @Test
    public void testRead() {
        //JsonHpoParser parser = new JsonHpoParser(smallHpo.getAbsolutePath());
    }

}
