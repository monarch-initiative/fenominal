package org.monarchinitiative.fenominal.gui.output;

import org.monarchinitiative.fenominal.gui.model.CaseReport;
import org.monarchinitiative.fenominal.gui.model.FenominalTerm;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.stream.Collectors;

public class CaseReportTsvOutputter implements PhenoOutputter{

    private final CaseReport caseReport;

    public CaseReportTsvOutputter(CaseReport caseReport) {
        this.caseReport = caseReport;
    }

    @Override
    public void output(Writer writer) throws IOException {
        List<String> rows = caseReport.getTerms()
                .stream()
                .map(FenominalTerm::toString)
                .collect(Collectors.toList());
        for (var row: rows) {
            writer.write(row + "\n");
        }
    }
}
