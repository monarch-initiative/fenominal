package org.monarchinitiative.fenominal.gui.output;

import com.google.protobuf.util.JsonFormat;
import org.monarchinitiative.fenominal.core.FenominalRunTimeException;
import org.monarchinitiative.fenominal.gui.model.MedicalEncounter;
import org.monarchinitiative.fenominal.gui.model.PhenopacketModel;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.phenopackets.phenotools.builder.PhenopacketBuilder;
import org.phenopackets.phenotools.builder.builders.MetaDataBuilder;
import org.phenopackets.phenotools.builder.builders.PhenotypicFeatureBuilder;
import org.phenopackets.phenotools.builder.builders.Resources;
import org.phenopackets.phenotools.builder.builders.TimeElements;
import org.phenopackets.schema.v2.Phenopacket;
import org.phenopackets.schema.v2.core.MetaData;
import org.phenopackets.schema.v2.core.PhenotypicFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Writer;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

public class PhenopacketJsonOutputter implements PhenoOutputter{
    private static final Logger LOGGER = LoggerFactory.getLogger(PhenopacketJsonOutputter.class);

    private final PhenopacketModel phenopacketModel;

    public PhenopacketJsonOutputter(PhenopacketModel phenopacketModel) {
        this.phenopacketModel = phenopacketModel;
    }

    @Override
    public void output(Writer writer) throws IOException {
        LocalDate nowDate = LocalDate.now();
        MetaData meta = MetaDataBuilder
                .create(LocalDate.now().toString(), "TODO-biocurator ID")
                .resource(Resources.hpoVersion("TODO"))
                .build();
        PhenopacketBuilder builder =PhenopacketBuilder.create(generatePhenopacketId(), meta);
        List<MedicalEncounter> encounters = phenopacketModel.getEncounters();
        List<LocalDate> encounterDates = phenopacketModel.getEncounterDates();
        if (encounterDates.size() != encounters.size()) {
            // should never happen, sanity check
            throw new FenominalRunTimeException("Mismatched encounter and encounterDates list");
        }
        for (int i = 0; i < encounters.size(); i++) {
            LocalDate encounterDate = encounterDates.get(i);
            String isoAge = iso8601(encounterDate);
            MedicalEncounter encounter = encounters.get(i);
            for (var phenotype : encounter.getTerms()) {
                Term term = phenotype.getTerm();
                boolean observed = phenotype.isObserved();
                PhenotypicFeature pf;
                if (observed)
                    pf = PhenotypicFeatureBuilder
                            .create(term.getId().getValue(), term.getName())
                            .onset(TimeElements.age(isoAge))
                            .build();
                else
                    pf = PhenotypicFeatureBuilder
                            .create(term.getId().getValue(), term.getName())
                            .onset(TimeElements.age(isoAge))
                            .excluded()
                            .build();
                builder.phenotypicFeature(pf); // add feature, one at a time
            }
        }
        // The Phenopacket is now complete and we would like to write it as JSON
        Phenopacket phenopacket = builder.build();
        String json =  JsonFormat.printer().print(phenopacket);
        writer.write(json);
    }

    private String iso8601(LocalDate encounterDate) {
        // Period.between takes (start, end), inclusive
        if (phenopacketModel.getBirthdate() == null) {
            LOGGER.error("Could not get phenopacket birthdate");
            return "P0";
        }
        if (encounterDate == null) {
            LOGGER.error("Could not get encounterDate");
            return "P0";
        }

        Period diff = Period.between( phenopacketModel.getBirthdate(),encounterDate);
        int y =diff.getYears();
        int m = diff.getMonths();
        int d = diff.getDays();
        return "P" + y + "Y" + m + "M" + d + "D";
    }

    private String generatePhenopacketId() {
        String date = LocalDate.now().toString();
        return date + "-fenominal";
    }
}
