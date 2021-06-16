package org.monarchinitiative.fenominal.json;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.File;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class JsonHpoParserTest {
    private final static File smallHpo = Paths.get("src/test/resources/hpo/hp_head.json").toFile();
    private final static JsonHpoParser parser = new JsonHpoParser(smallHpo.getAbsolutePath());
    private final static Ontology ontology = parser.getHpo();


    private static Stream<Arguments> shouldReturnExpectedResultWhenGivenRightInput() {
        return Stream.of(
                Arguments.of(TermId.of("HP:0000001"), true),
                Arguments.of(TermId.of("HP:0000005"), true),
                Arguments.of(TermId.of("HP:0000006"), true),
                Arguments.of(TermId.of("HP:0000007"), true),
                Arguments.of(TermId.of("HP:0000118"), true),
                Arguments.of(TermId.of("HP:0012372"),true),
                Arguments.of(TermId.of("HP:0012373"),true),
                Arguments.of(TermId.of("HP:0012374"),true),
                Arguments.of(TermId.of("HP:0100886"),true),
                Arguments.of(TermId.of("HP:0100887"),true),
                Arguments.of(TermId.of("HP:0000528"),true),
                Arguments.of(TermId.of("HP:0000568"),true),
                Arguments.of(TermId.of("HP:0000632"),true),
                Arguments.of(TermId.of("HP:0007686"),true),
                Arguments.of(TermId.of("HP:0040279"),true),
                Arguments.of(TermId.of("HP:0040280"),true),
                Arguments.of(TermId.of("HP:0040281"),true),
                Arguments.of(TermId.of("HP:0040282"),true),
                Arguments.of(TermId.of("HP:0040283"),true),
                Arguments.of(TermId.of("HP:0040284"),true),
                Arguments.of(TermId.of("HP:0040285"),true),
                Arguments.of(TermId.of("HP:0000478"), true),
                Arguments.of(TermId.of("HP:FAKE"), false)
        );
    }



    @ParameterizedTest
    @MethodSource
    void shouldReturnExpectedResultWhenGivenRightInput(TermId input, boolean expected) {
        // When
        boolean result = ontology.containsTerm(input);
        // Then
        assertEquals(expected, result);
    }




}
