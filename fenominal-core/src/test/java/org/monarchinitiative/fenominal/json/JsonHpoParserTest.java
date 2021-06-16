package org.monarchinitiative.fenominal.json;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class JsonHpoParserTest {
    private final static File smallHpo = Paths.get("src/test/resources/hpo/hp_head.json").toFile();

    @Test
    public void testRead() {
        int x=42;
        JsonHpoParser parser = new JsonHpoParser(smallHpo.getAbsolutePath());
        assertNotNull(parser.getHpo());
    }

}
