package org.monarchinitiative.fenominal.gui.model;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO RETHINK THE INTERFACE
 * In addition to a list of {@link FenominalTerm} objects we need to pass in the datetime.
 * Ask Daniel how the new HCA does this
 */
public class PhenopacketModel implements TextMiningResultsModel {

    private final LocalDate birthdate;

    private List<LocalDate> encounterDates;


    private final List<MedicalEncounter> encounters;


    public PhenopacketModel(LocalDate bdate) {
        birthdate = bdate;
        encounters = new ArrayList<>();
        encounterDates = new ArrayList<>();
    }



    @Override
    public void addHpoFeatures(List<FenominalTerm> terms) {
        encounters.add(new MedicalEncounter(terms));
    }

    @Override
    public int casesMined() {
        return 0;
    }

    @Override
    public int getTermCount() {
        return 0;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public List<LocalDate> getEncounterDates() {
        return encounterDates;
    }

}
