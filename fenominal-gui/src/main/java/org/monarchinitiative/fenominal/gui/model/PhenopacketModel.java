package org.monarchinitiative.fenominal.gui.model;


import java.util.ArrayList;
import java.util.List;

/**
 * TODO RETHINK THE INTERFACE
 * In addition to a list of {@link FenominalTerm} objects we need to pass in the datetime.
 * Ask Daniel how the new HCA does this
 */
public class PhenopacketModel implements TextMiningResultsModel {


    private final List<MedicalEncounter> encounters;


    public PhenopacketModel() {
        encounters = new ArrayList<>();
    }


    @Override
    public void output() {

    }

    @Override
    public List<String> getTsv() {
        return null;
    }

    @Override
    public void addHpoFeatures(List<FenominalTerm> terms) {
        encounters.add(new MedicalEncounter(terms));
    }

    @Override
    public int minedSoFar() {
        return 0;
    }

    @Override
    public int getTermCount() {
        return 0;
    }

}
