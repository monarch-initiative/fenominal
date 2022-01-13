package org.monarchinitiative.fenominal.gui.output;

import com.google.protobuf.util.JsonFormat;
import org.monarchinitiative.fenominal.core.FenominalRunTimeException;
import org.monarchinitiative.fenominal.gui.model.MedicalEncounter;
import org.monarchinitiative.fenominal.gui.model.PhenopacketByAgeModel;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.phenopackets.phenotools.builder.PhenopacketBuilder;
import org.phenopackets.phenotools.builder.builders.MetaDataBuilder;
import org.phenopackets.phenotools.builder.builders.PhenotypicFeatureBuilder;
import org.phenopackets.phenotools.builder.builders.Resources;
import org.phenopackets.phenotools.builder.builders.TimeElements;
import org.phenopackets.schema.v2.Phenopacket;
import org.phenopackets.schema.v2.core.MetaData;
import org.phenopackets.schema.v2.core.PhenotypicFeature;

import java.io.IOException;
import java.io.Writer;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public record PhenopacketByAgeJsonOutputter(
        PhenopacketByAgeModel phenopacketModel) implements PhenoOutputter {

    @Override
    public void output(Writer writer) throws IOException {
        Map<String,String> data = phenopacketModel.getModelData();
        String biocurator = data.getOrDefault("biocurator", "n/a");
        String hpoVersion = data.getOrDefault("HPO", "n/a");
        MetaData meta = MetaDataBuilder
                .create(LocalDate.now().toString(), biocurator)
                .resource(Resources.hpoVersion(hpoVersion))
                .build();
        PhenopacketBuilder builder = PhenopacketBuilder.create(generatePhenopacketId(), meta);
        List<MedicalEncounter> encounters = phenopacketModel.getEncounters();
        List<String> encounterAges = phenopacketModel.getEncounterAges();
        if (encounterAges.size() != encounters.size()) {
            // should never happen, sanity check
            throw new FenominalRunTimeException("Mismatched encounter and encounterDates list");
        }
        for (int i = 0; i < encounters.size(); i++) {
            String isoAge = encounterAges.get(i);
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
        String json = JsonFormat.printer().print(phenopacket);
        writer.write(json);
    }

    private String generatePhenopacketId() {
        String date = LocalDate.now().toString();
        return date + "-fenominal";
    }
}
