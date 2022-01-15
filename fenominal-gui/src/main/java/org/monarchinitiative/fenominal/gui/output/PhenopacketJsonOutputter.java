package org.monarchinitiative.fenominal.gui.output;

import com.google.protobuf.util.JsonFormat;
import org.monarchinitiative.fenominal.gui.model.FenominalTerm;
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
import java.util.Map;

import static org.monarchinitiative.fenominal.gui.config.FenominalConfig.BIOCURATOR_ID_PROPERTY;
import static org.monarchinitiative.fenominal.gui.config.FenominalConfig.HPO_VERSION_KEY;

public record PhenopacketJsonOutputter(PhenopacketModel phenopacketModel)
        implements PhenoOutputter{
    private static final Logger LOGGER = LoggerFactory.getLogger(PhenopacketJsonOutputter.class);

    @Override
    public void output(Writer writer) throws IOException {
        Map<String,String> data = phenopacketModel.getModelData();
        String biocurator = data.getOrDefault(BIOCURATOR_ID_PROPERTY, "n/a");
        String hpoVersion = data.getOrDefault(HPO_VERSION_KEY, "n/a");
        MetaData meta = MetaDataBuilder
                .create(LocalDate.now().toString(), biocurator)
                .resource(Resources.hpoVersion(hpoVersion))
                .build();
        PhenopacketBuilder builder =PhenopacketBuilder.create(generatePhenopacketId(), meta);
        for (FenominalTerm fenominalTerm : phenopacketModel.getTerms()) {
            Term term = fenominalTerm.getTerm();
            boolean observed = fenominalTerm.isObserved();
            PhenotypicFeature pf;
            if (fenominalTerm.hasAge() && observed) {
                String isoAge = fenominalTerm.getIso8601Age();
                pf = PhenotypicFeatureBuilder
                        .create(term.getId().getValue(), term.getName())
                        .onset(TimeElements.age(isoAge))
                        .build();
            } else if (fenominalTerm.hasAge() ){
                String isoAge = fenominalTerm.getIso8601Age();
                pf = PhenotypicFeatureBuilder
                        .create(term.getId().getValue(), term.getName())
                        .onset(TimeElements.age(isoAge))
                        .excluded()
                        .build();
            } else if (observed) {
                pf = PhenotypicFeatureBuilder
                        .create(term.getId().getValue(), term.getName())
                        .build();
            } else {
                pf = PhenotypicFeatureBuilder
                        .create(term.getId().getValue(), term.getName())
                        .excluded()
                        .build();
            }
            builder.phenotypicFeature(pf); // add feature, one at a time
        }
        // TODO Metadata and updates

        // The Phenopacket is now complete and we would like to write it as JSON
        Phenopacket phenopacket = builder.build();
        String json =  JsonFormat.printer().print(phenopacket);
        writer.write(json);
    }



    private String generatePhenopacketId() {
        String date = LocalDate.now().toString();
        return date + "-fenominal";
    }
}
