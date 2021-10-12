package org.monarchinitiative.fenominal.gui.model;

import org.monarchinitiative.fenominal.gui.output.PhenoOutputter;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

public interface TextMiningResultsModel {

    /**
     * Handle for outputting the text mining results. Writer can be a File handle or a StringWriter
     * @param outputter Interface for outputting heterogeneous models with a desired output format
     * @param writer handle for writing
     */
    default void output(PhenoOutputter outputter, Writer writer) throws IOException {
        outputter.output(writer);
    }

    //List<String> getTsv();

    void addHpoFeatures(List<FenominalTerm> terms);

    int casesMined();

    int getTermCount();

}
