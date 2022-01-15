package org.monarchinitiative.fenominal.gui.model;

import com.google.protobuf.Timestamp;
import org.monarchinitiative.fenominal.gui.io.PhenopacketImporter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AbstractPhenopacketModel {

    protected final String id;

    protected final List<FenominalTerm> terms;


    protected final Map<String, String> data;

    /** If we initialize the model from a preexisting model, then we initialize this to the
     * createdOn timestamp from the phenopacket, otherwise this remains null
     */
    protected final Timestamp createdOn;
    /** If we initialize the model from a preexisting model, then we initialize this to the
     * createdBy element from the phenopacket, otherwise this remains null
     */
    protected final String createdBy;
    /** If we initialize the model from a preexisting model, then we initialize this to the
     * Phenopacket updates from the phenopacket, otherwise this remains null
     */
    protected final List<SimpleUpdate> updates;

    public AbstractPhenopacketModel(PhenopacketImporter phenopacketImp){
        this.id = phenopacketImp.getId();
        this.terms = phenopacketImp.getFenominalTermList();
        this.createdBy = phenopacketImp.getCreatedBy();
        this.createdOn = phenopacketImp.getCreatedOn();
        this.updates = phenopacketImp.getUpdateList();
        data = new HashMap<>();
    }

    public AbstractPhenopacketModel(String id) {
        this.id = id;
        this.createdBy = null;
        this.createdOn = null;
        this.updates = List.of();
        this.terms = new ArrayList<>();
        data = new HashMap<>();
    }

    public String getId() {
        return id;
    }

    public List<FenominalTerm> getTerms() {
        return terms;
    }

    public Map<String, String> getData() {
        return data;
    }

    public Timestamp getCreatedOn() {
        return createdOn;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public List<SimpleUpdate> getUpdates() {
        return updates;
    }



    public String getPhenopacketId() { return id; }

    public boolean isUpdateOfExistingPhenopacket() {
        return this.createdBy != null;
    }
}
