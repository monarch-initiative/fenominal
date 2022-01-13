package org.monarchinitiative.fenominal.gui.output;

import org.monarchinitiative.fenominal.gui.model.OneByOneCohort;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.IOException;
import java.io.Writer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;


/**
 * Output in PhenoteFX format to simplify transfer
 * #diseaseID	diseaseName	phenotypeID	phenotypeName	onsetID	onsetName	frequency	sex	negation	modifier	description	publication	evidence	biocuration
 * OMIM:617391	Epileptic encephalopathy, early infantile, 54	HP:0000252	Microcephaly							OMIM-CS:headandneckhead > SMALL HEAD CIRCUMFERENCE	OMIM:617391	TAS	HPO:skoehler[2017-07-13]
 */
public class PhenoteFxTsvOutputter  implements PhenoOutputter {

    private final OneByOneCohort cohort;
    private final String pmid;
    private final String diseaseId;
    private final String diseaseName;
    private final String biocurationentry;

    private final static String EMPTY_STRING = "";

    public PhenoteFxTsvOutputter(OneByOneCohort cohort, String biocuratorId) {
        this.cohort = cohort;
        this.pmid = cohort.getPmid();
        this.diseaseId = cohort.getOmimId();
        this.diseaseName = cohort.getDiseasename();
        // get current date for biocuration entry
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        String todaysDate = today.format(formatter);
        // HPO:skoehler[2018-10-08]
        biocurationentry = String.format("%s[%s]", biocuratorId, today);
    }


    private String getCountRow(String hpoId, String hpoLabel, String frequencyString) {
        final String onsetID = EMPTY_STRING;
        final String onsetLabel = EMPTY_STRING;
        final String sex = EMPTY_STRING;
        final String negation = EMPTY_STRING;
        final String modifier = EMPTY_STRING;
        final String description = EMPTY_STRING;
        final String evidence = "PCS";

        String[] s ={
                diseaseId,
                diseaseName,
                hpoId,
                hpoLabel,
                onsetID,
                onsetLabel,
                frequencyString,
                sex,
                negation,
                modifier,
                description,
                pmid,
                evidence,
                biocurationentry};
        return String.join("\t", s);
    }

    /**
     * This is the header of the small files.
     * @return small file header.
     */
    public static String getHeader() {
        String []fields={"#diseaseID",
                "diseaseName",
                "phenotypeID",
                "phenotypeName",
                "onsetID",
                "onsetName",
                "frequency",
                "sex",
                "negation",
                "modifier",
                "description",
                "publication",
                "evidence",
                "biocuration"};
        return String.join("\t", fields);
    }

    @Override
    public void output(Writer writer) throws IOException {
        Map<TermId, Integer> termCountsMap = cohort.getCountsMap();
        Map<TermId, String> termLabelMap = cohort.getLabelMap();
        int total = cohort.casesMined();

        writer.write(getHeader() + "\n");
        for (TermId tid : termCountsMap.keySet()) {
            String termId = tid.getValue();
            String label = termLabelMap.get(tid);
            int count = termCountsMap.get(tid);
            if (count > total) {
                //TODO -- need to open up an error dialog
                System.err.println("[ERROR incorrect counts");
            }
            String frequency = String.format("%d/%d", count, total);
            String row = getCountRow(termId, label, frequency);
            writer.write(row + "\n");
        }
    }
}
