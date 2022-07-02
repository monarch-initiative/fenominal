package org.monarchinitiative.fenominal.gui.model;

import com.google.protobuf.Timestamp;
import org.monarchinitiative.fenominal.gui.io.PhenopacketImporter;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

import static org.monarchinitiative.fenominal.gui.config.FenominalConfig.N_CURATED_KEY;
import static org.monarchinitiative.fenominal.gui.config.FenominalConfig.PATIENT_ID_KEY;

public class AbstractPhenopacketModel implements TextMiningResultsModel {

    protected final String id;

    protected final List<FenominalTerm> terms;
    private int caseMined = 0;
    /** is the data "dirty", i.e., not saved yet? */
    private boolean changed;

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

    private final Sex sex;

    public AbstractPhenopacketModel(PhenopacketImporter phenopacketImp){
        this.id = phenopacketImp.getId();
        this.sex = phenopacketImp.sex();
        this.terms = phenopacketImp.getFenominalTermList();
        this.createdBy = phenopacketImp.getCreatedBy();
        this.createdOn = phenopacketImp.getCreatedOn();
        this.updates = phenopacketImp.getUpdateList();
        data = new HashMap<>();
        data.put(PATIENT_ID_KEY, id);
    }

    public AbstractPhenopacketModel(String id, Sex sex) {
        this.id = id;
        this.sex = sex;
        this.createdBy = null;
        this.createdOn = null;
        this.updates = List.of();
        this.terms = new ArrayList<>();
        data = new HashMap<>();
        data.put(PATIENT_ID_KEY, id);
        changed = false;
    }

    public String getId() {
        return id;
    }

    public Sex sex() {return this.sex; }


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

    @Override
    public void addHpoFeatures(List<FenominalTerm> fterms) {
        caseMined++;
        changed = true;
        terms.addAll(fterms);
        setModelDataItem(N_CURATED_KEY, String.valueOf(getTermCount()));
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
                .map(Term::id)
                .collect(Collectors.toSet());
        return tids.size();
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


    public List<String> getEncounterAges() {
        Set<String> ageSet = new HashSet<>();
        for (FenominalTerm fenominalTerm : terms) {
            if (fenominalTerm.hasAge()) {
                ageSet.add(fenominalTerm.getIso8601Age());
            }
        }
        List<String> ageList = new ArrayList<>(ageSet);
        Collections.sort(ageList);
        return ageList;
    }

    @Override
    public Map<String, String> getModelData() {
        return data;
    }

    @Override
    public void setModelDataItem(String k, String v) {
        changed = true;
        data.put(k,v);
    }

    @Override
    public boolean isChanged() {
        return changed;
    }

    @Override
    public void resetChanged() {
        changed = false;
    }


    public String getPhenopacketId() { return id; }

    public boolean isUpdateOfExistingPhenopacket() {
        return this.createdBy != null;
    }
}
