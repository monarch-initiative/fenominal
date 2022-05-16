package org.monarchinitiative.fenominal.gui.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.monarchinitiative.fenominal.gui.config.FenominalConfig.PATIENT_ID_KEY;

public interface TextMiningResultsModel {

    void addHpoFeatures(List<FenominalTerm> terms);

    int casesMined();

    int getTermCount();

    default Sex sex() {
        return Sex.UNKNOWN_SEX;
    }

    Map<String,String> getModelData();

    void setModelDataItem(String k, String v);

    default String getInitialFileName() {
        Map<String, String> data = getModelData();
        if (data.containsKey(PATIENT_ID_KEY)) {
            return data.get(PATIENT_ID_KEY) + "-" + "fenominal.json";
        } else {
            return "fenominal.json";
        }
    }

    /**
     * The {@link PhenopacketModel} stores a birthdate to calculate ages. It is the
     * only one of the four model types tp need this information. Here we define
     * an Optional that will be empty for the other {@link TextMiningResultsModel} objects
     * but should hold the birth data for the {@link PhenopacketModel}. It is a little tricky
     * because of the option to import existing phenopackets (that do not contain the birhtdate) -
     * the user is required to add this information to the model.
     * @return Optional with the birthdate of the subject of the Phenopacket
     */
    default Optional<LocalDate> getBirthdate() {
        return Optional.empty();
    }

    default void setBirthdate(LocalDate birthdate) {
        // no op
    }

    boolean isChanged();
    void resetChanged();
}
