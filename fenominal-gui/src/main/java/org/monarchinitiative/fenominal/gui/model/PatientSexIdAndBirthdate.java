package org.monarchinitiative.fenominal.gui.model;

import java.time.LocalDate;

/**
 * POJO for returning results about patient sex and the anonymized ID and the birthdate
 */
public record PatientSexIdAndBirthdate(Sex sex, String id, LocalDate birthdate) {
}
