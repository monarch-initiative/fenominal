package org.monarchinitiative.fenominal.gui.model;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Model for building a Phenopacket with multiple time points for a single patient
 * @author Peter N Robinson
 */
public class PhenopacketModel implements TextMiningResultsModel {

    private final LocalDate birthdate;

    private final List<LocalDate> encounterDates;


    private final List<MedicalEncounter> encounters;



    public PhenopacketModel(LocalDate bdate) {
        birthdate = bdate;
        encounters = new ArrayList<>();
        encounterDates = new ArrayList<>();
    }


    @Override
    public void addHpoFeatures(List<FenominalTerm> terms) {
        // should never happen
        throw new UnsupportedOperationException("Phenopackets must use overloaded function with LocalDate");
    }

    public void addHpoFeatures(List<FenominalTerm> terms, LocalDate date) {
        MedicalEncounter encounter = new MedicalEncounter(terms);
        encounterDates.add(date);
        encounters.add(encounter);
        System.out.println("Adding HPO, encounters " + encounters.size() + " dates "+encounterDates.size());
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

    public List<MedicalEncounter> getEncounters() {
        return encounters;
    }

    @Override
    public String toString() {
        return String.format("[PhenopacketModel] birthdate=%s; encounters:%d, dates: %d\n",
               getBirthdate(), encounters.size() ,encounterDates.size());
    }
}
