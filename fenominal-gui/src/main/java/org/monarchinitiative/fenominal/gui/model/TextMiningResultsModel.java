package org.monarchinitiative.fenominal.gui.model;

import org.monarchinitiative.fenominal.gui.output.PhenoOutputter;

import java.io.IOException;
import java.io.Writer;
import java.time.LocalDate;
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

    void addHpoFeatures(List<FenominalTerm> terms);

    /** currently only used by Phenopacket, but shold be expanded. */
    default void addHpoFeatures(List<FenominalTerm> terms, LocalDate date) {}

    int casesMined();

    int getTermCount();

}
