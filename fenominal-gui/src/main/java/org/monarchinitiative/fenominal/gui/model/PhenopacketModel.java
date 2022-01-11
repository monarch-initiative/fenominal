package org.monarchinitiative.fenominal.gui.model;


import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Model for building a Phenopacket with multiple time points for a single patient
 * @author Peter N Robinson
 */
public class PhenopacketModel implements TextMiningResultsModel {

    private final String id;

    private final LocalDate birthdate;

    private final List<LocalDate> encounterDates;


    private final List<MedicalEncounter> encounters;

    private final Map<String, String> data;

    public PhenopacketModel(LocalDate bdate, String id) {
        birthdate = bdate;
        this.id = id;
        encounters = new ArrayList<>();
        encounterDates = new ArrayList<>();
        data = new HashMap<>();
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
    }

    @Override
    public int casesMined() {
        return 0;
    }

    @Override
    public int getTermCount() {
        if (encounters.size()==0) return 0;
        Set<TermId> tids = encounters.stream().map(MedicalEncounter::getTerms)
                .flatMap(List::stream)
                .map(FenominalTerm::getTerm)
                .map(Term::getId)
                .collect(Collectors.toSet());
        return tids.size();
    }

    @Override
    public Map<String, String> getModelData() {
        return data;
    }

    @Override
    public void setModelDataItem(String k, String v) {
        data.put(k,v);
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public String getPhenopacketId() { return id; }

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
