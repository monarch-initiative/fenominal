package org.monarchinitiative.fenominal.gui.model;

public enum Sex {
    UNKNOWN_SEX, FEMALE, MALE, OTHER_SEX;

    public static Sex fromString(String sex) {
        return switch (sex) {
            case "other" -> OTHER_SEX;
            case "female" -> FEMALE;
            case "male" -> MALE;
            default -> UNKNOWN_SEX;
        };
    }
}
