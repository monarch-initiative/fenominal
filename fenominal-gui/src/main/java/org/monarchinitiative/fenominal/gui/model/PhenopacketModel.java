package org.monarchinitiative.fenominal.gui.model;


import com.google.protobuf.Timestamp;
import org.monarchinitiative.fenominal.gui.io.PhenopacketImporter;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Model for building a Phenopacket with multiple time points for a single patient
 * @author Peter N Robinson
 */
public class PhenopacketModel extends AbstractPhenopacketModel implements TextMiningResultsModel {


    private LocalDate birthdate = null;


    private int caseMined = 0;

    /**
     * This constructor is use to initialize a Phenopacket for the first time (i.e., not from
     * a preexisting phenopacket).
     * @param id id for the phenopacket
     */
    public PhenopacketModel(String id) {
        super(id);
    }

    /**
     * This constructor is used to initialize the PhenopacketModel from a pre-exisiting phenopacket
     * @param phenopacketImp Parsed pre-existing Phenopacket
     */
    public PhenopacketModel(PhenopacketImporter phenopacketImp) {
        super(phenopacketImp);
    }


    @Override
    public void addHpoFeatures(List<FenominalTerm> fterms) {
        caseMined++;
        terms.addAll(fterms);
    }


    @Override
    public int casesMined() {
        return caseMined;
    }

    @Override
    public int getTermCount() {
        if (terms.size()==0) return 0;
        Set<TermId> tids = terms.stream()
                .map(FenominalTerm::getTerm)
                .map(Term::getId)
                .collect(Collectors.toSet());
        return tids.size();
    }



    @Override
    public String toString() {
        return String.format("[PhenopacketModel] terms:%d.",terms.size());
    }

    @Override
    public  Optional<LocalDate> getBirthdate() {
        return Optional.ofNullable(this.birthdate);
    }

    @Override
    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public List<FenominalTerm> getTerms() {
        return terms;
    }

    public List<LocalDate> getEncounterDates(LocalDate birthdate) {
        Set<Period> ageSet = new HashSet<>();
        for (FenominalTerm fterm : terms) {
            if (fterm.hasAge()) {
                ageSet.add(Period.parse(fterm.getIso8601Age()));
            }
        }
        List<LocalDate> encounterDates = new ArrayList<>();
        for (Period age : ageSet) {
            LocalDate ldate = birthdate
                    .plusYears(age.getYears())
                    .plusMonths(age.getMonths())
                    .plusDays(age.getDays());
            encounterDates.add(ldate);
        }
        Collections.sort(encounterDates);
        return encounterDates;
    }

    @Override
    public Map<String, String> getModelData() {
        return data;
    }

    @Override
    public void setModelDataItem(String k, String v) {
        data.put(k,v);
    }



}
