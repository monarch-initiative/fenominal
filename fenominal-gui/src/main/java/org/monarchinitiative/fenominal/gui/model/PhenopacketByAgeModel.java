package org.monarchinitiative.fenominal.gui.model;

import org.monarchinitiative.fenominal.gui.io.PhenopacketImporter;

/**
 * This class represents a Phenopacket whereby the user enters ages explicitly
 * (i.e., not via subtracting encounter dates from the birthdate).
 * @author Peter N Robinson
 */
public class PhenopacketByAgeModel extends AbstractPhenopacketModel {

    public PhenopacketByAgeModel(String id, Sex sex) {
        super(id, sex);
    }

    public PhenopacketByAgeModel(PhenopacketImporter importer) {
        super(importer);
    }
}
