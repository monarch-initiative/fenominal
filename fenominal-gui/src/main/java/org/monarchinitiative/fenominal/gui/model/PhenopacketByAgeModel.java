package org.monarchinitiative.fenominal.gui.model;

import org.monarchinitiative.fenominal.gui.output.PhenoOutputter;

import java.io.IOException;
import java.io.Writer;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PhenopacketByAgeModel  implements TextMiningResultsModel {

    private final List<String> encounterAges;


    private final List<MedicalEncounter> encounters;

    public PhenopacketByAgeModel() {
        encounterAges = new ArrayList<>();
        encounters = new ArrayList<>();
    }

    public List<String> getEncounterAges() {
        return encounterAges;
    }

    public List<MedicalEncounter> getEncounters() {
        return encounters;
    }

    @Override
    public void addHpoFeatures(List<FenominalTerm> terms) {
        throw new UnsupportedOperationException("PhenopacketByAgeModel requires iso8601 strings");
    }

    @Override
    public void addHpoFeatures(List<FenominalTerm> terms, LocalDate date) {
        throw new UnsupportedOperationException("PhenopacketByAgeModel only accepts iso8601 strings");
    }
    @Override
    public void addHpoFeatures(List<FenominalTerm> terms, String isoAge) {
        MedicalEncounter encounter = new MedicalEncounter(terms);
        encounterAges.add(isoAge);
        encounters.add(encounter);
    }

    @Override
    public int casesMined() {
        return encounters.size();
    }

    @Override
    public int getTermCount() {
        return encounters.stream()
                .map(MedicalEncounter::getTerms)
                .flatMap(List::stream)
                .map(FenominalTerm::getTerm)
                .collect(Collectors.toSet())
                .size();
    }
}
