package org.monarchinitiative.fenominal.gui.model;

import org.monarchinitiative.fenominal.gui.io.PhenopacketImporter;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Model for building a Phenopacket with multiple time points for a single patient
 * @author Peter N Robinson
 */
public class PhenopacketModel extends AbstractPhenopacketModel  {

    private LocalDate birthdate = null;

    /**
     * This constructor is use to initialize a Phenopacket for the first time (i.e., not from
     * a preexisting phenopacket).
     * @param id id for the phenopacket
     */
    public PhenopacketModel(String id,Sex sex) {
        super(id, sex);
    }

    /**
     * This constructor is used to initialize the PhenopacketModel from a pre-exisiting phenopacket
     * @param phenopacketImp Parsed pre-existing Phenopacket
     */
    public PhenopacketModel(PhenopacketImporter phenopacketImp) {
        super(phenopacketImp);
    }

    @Override
    public String toString() {
        return String.format("[PhenopacketModel] terms:%d.",terms.size());
    }

    @Override
    public Optional<LocalDate> getBirthdate() {
        return Optional.ofNullable(this.birthdate);
    }

    @Override
    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

}
