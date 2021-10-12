package org.monarchinitiative.fenominal.gui.output;

import org.monarchinitiative.fenominal.gui.model.PhenopacketModel;

import java.io.IOException;
import java.io.Writer;

public class PhenopacketJsonOutputter implements PhenoOutputter{

    private final PhenopacketModel phenopacketModel;

    public PhenopacketJsonOutputter(PhenopacketModel phenopacketModel) {
        this.phenopacketModel = phenopacketModel;
    }

    @Override
    public void output(Writer writer) throws IOException {

    }
}
