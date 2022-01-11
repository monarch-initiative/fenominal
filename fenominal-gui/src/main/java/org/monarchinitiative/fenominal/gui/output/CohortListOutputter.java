package org.monarchinitiative.fenominal.gui.output;

import org.monarchinitiative.fenominal.gui.model.OneByOneCohort;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

public record CohortListOutputter(OneByOneCohort cohort) implements PhenoOutputter {

    @Override
    public void output(Writer writer) throws IOException {
        Map<TermId, Integer> countsMap = cohort.getCountsMap();
        Map<TermId, String> labelMap = cohort.getLabelMap();
        int N = cohort.casesMined();
        writer.write(String.format("%s\t%s\t%s\n", "Id", "Term", "Counts"));
        for (var e : countsMap.entrySet()) {
            TermId tid = e.getKey();
            String label = labelMap.get(tid);
            String counts = String.format("%d/%d", e.getValue(), N);
            writer.write(String.format("%s\t%s\t%s\n", tid.getValue(), label, counts));
        }
    }
}
