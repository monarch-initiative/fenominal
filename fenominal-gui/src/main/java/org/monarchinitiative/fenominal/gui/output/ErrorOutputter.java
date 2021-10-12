package org.monarchinitiative.fenominal.gui.output;

import java.io.IOException;
import java.io.Writer;

public class ErrorOutputter implements PhenoOutputter{
    @Override
    public void output(Writer writer) throws IOException {
        writer.write("Error with fenominal output");
    }
}
