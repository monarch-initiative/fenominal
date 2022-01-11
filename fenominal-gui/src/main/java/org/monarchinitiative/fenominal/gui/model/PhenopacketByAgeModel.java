package org.monarchinitiative.fenominal.gui.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PhenopacketByAgeModel  implements TextMiningResultsModel {

    private final List<String> encounterAges;


    private final List<MedicalEncounter> encounters;

    private final Map<String, String> data;

    public PhenopacketByAgeModel() {
        encounterAges = new ArrayList<>();
        encounters = new ArrayList<>();
        data = new HashMap<>();
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

    @Override
    public Map<String, String> getModelData() {
        return data;
    }

    @Override
    public void setModelDataItem(String k, String v) {
        data.put(k, v);
    }
}
