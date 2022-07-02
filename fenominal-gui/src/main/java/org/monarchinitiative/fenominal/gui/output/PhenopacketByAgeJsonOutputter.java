package org.monarchinitiative.fenominal.gui.output;

//import com.fasterxml.jackson.annotation.JsonFormat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;


import com.google.protobuf.Timestamp;
import org.monarchinitiative.fenominal.gui.model.FenominalTerm;
import org.monarchinitiative.fenominal.gui.model.PhenopacketByAgeModel;
import org.monarchinitiative.fenominal.gui.model.SimpleUpdate;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.phenopackets.phenopackettools.builder.PhenopacketBuilder;
import org.phenopackets.phenopackettools.builder.builders.MetaDataBuilder;
import org.phenopackets.phenopackettools.builder.builders.PhenotypicFeatureBuilder;
import org.phenopackets.phenopackettools.builder.builders.Resources;
import org.phenopackets.phenopackettools.builder.builders.TimeElements;
import org.phenopackets.schema.v2.Phenopacket;
import org.phenopackets.schema.v2.core.Individual;
import org.phenopackets.schema.v2.core.MetaData;
import org.phenopackets.schema.v2.core.PhenotypicFeature;
import org.phenopackets.schema.v2.core.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Writer;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.monarchinitiative.fenominal.gui.config.FenominalConfig.BIOCURATOR_ID_PROPERTY;
import static org.monarchinitiative.fenominal.gui.config.FenominalConfig.HPO_VERSION_KEY;

public record PhenopacketByAgeJsonOutputter(PhenopacketByAgeModel phenopacketModel)
        implements PhenoOutputter {
    private final static Logger LOGGER = LoggerFactory.getLogger(PhenopacketByAgeJsonOutputter.class);

    private MetaData getMetaData() {
        Map<String,String> data = phenopacketModel.getModelData();
        String biocurator = data.getOrDefault(BIOCURATOR_ID_PROPERTY, "n/a");
        String hpoVersion = data.getOrDefault(HPO_VERSION_KEY, "n/a");
        if (phenopacketModel.isUpdateOfExistingPhenopacket()) {
            Timestamp createdOn = phenopacketModel.getCreatedOn();
            String createdBy = phenopacketModel.getCreatedBy();
            List<SimpleUpdate> simpleUpdates = phenopacketModel.getUpdates();
            // Now add an update for the current curation task
            // Take the current biocurator (default to createdBy, but this should always work
            String currentBiocurator = phenopacketModel.getModelData().getOrDefault(BIOCURATOR_ID_PROPERTY,createdBy);
            Instant now = Instant.now();
            Timestamp timestamp =
                    Timestamp.newBuilder().setSeconds(now.getEpochSecond())
                            .setNanos(now.getNano()).build();
            simpleUpdates.add(new SimpleUpdate(currentBiocurator, timestamp));
            List<Update> updates = new ArrayList<>();
            for (var supd : simpleUpdates) {
                Update upd = Update.newBuilder().setTimestamp(supd.createdOn()).setUpdatedBy(supd.createdBy()).build();
                updates.add(upd);
            }
            return MetaData.newBuilder()
                    .setCreated(createdOn)
                    .setCreatedBy(createdBy)
                    .addAllUpdates(updates)
                    .addResources(Resources.hpoVersion(hpoVersion))
                    .build();
        } else {
            return MetaDataBuilder
                    .builder(LocalDate.now().toString(), biocurator)
                    .addResource(Resources.hpoVersion(hpoVersion))
                    .build();
        }
    }



    @Override
    public void output(Writer writer) throws IOException {
        LOGGER.info("Output by age model with {} terms.", phenopacketModel.getTermCount());
        Map<String, String> data = phenopacketModel.getModelData();
        MetaData meta = getMetaData();
        PhenopacketBuilder builder = PhenopacketBuilder.create(generatePhenopacketId(), meta);
        org.phenopackets.schema.v2.core.Sex sx = switch (phenopacketModel.sex()) {
            case MALE -> org.phenopackets.schema.v2.core.Sex.MALE;
            case FEMALE -> org.phenopackets.schema.v2.core.Sex.FEMALE;
            case OTHER_SEX -> org.phenopackets.schema.v2.core.Sex.OTHER_SEX;
            default -> org.phenopackets.schema.v2.core.Sex.UNKNOWN_SEX;
        };
        Individual subject = Individual.newBuilder().setId(phenopacketModel.getId()).setSex(sx).build();
        for (FenominalTerm fterm : phenopacketModel.getTerms()) {
            Term term = fterm.getTerm();
            PhenotypicFeature pf;
            if (fterm.isObserved() && fterm.hasAge()) {
                pf = PhenotypicFeatureBuilder
                        .builder(term.getId().getValue(), term.getName())
                        .onset(TimeElements.age(fterm.getIso8601Age()))
                        .build();
            } else if (fterm.hasAge()) {
                pf = PhenotypicFeatureBuilder
                        .builder(term.getId().getValue(), term.getName())
                        .onset(TimeElements.age(fterm.getIso8601Age()))
                        .excluded()
                        .build();
            } else if (fterm.isObserved()) {
                pf = PhenotypicFeatureBuilder

                        .builder(term.getId().getValue(), term.getName())
                        .build();
            } else {
                pf = PhenotypicFeatureBuilder
                        .builder(term.getId().getValue(), term.getName())

                        .excluded()
                        .build();
            }
            builder.addPhenotypicFeature(pf); // add feature, one at a time
        }
        builder.individual(subject);

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
