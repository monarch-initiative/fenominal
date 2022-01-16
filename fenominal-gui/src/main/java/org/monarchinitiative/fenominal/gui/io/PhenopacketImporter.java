package org.monarchinitiative.fenominal.gui.io;

import com.google.protobuf.Timestamp;
import com.google.protobuf.util.JsonFormat;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.monarchinitiative.fenominal.core.FenominalRunTimeException;


import org.monarchinitiative.fenominal.gui.hpotextminingwidget.PhenotypeTerm;
import org.monarchinitiative.fenominal.gui.model.FenominalTerm;
import org.monarchinitiative.fenominal.gui.model.SimpleUpdate;
import org.monarchinitiative.phenol.base.PhenolRuntimeException;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.phenopackets.schema.v2.Phenopacket;
import org.phenopackets.schema.v2.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;


/**
 * This class ingests a phenopacket, which is required to additionally contain the
 * path of a VCF file that will be used for the analysis.
 *
 * @author Peter Robinson
 */
public class PhenopacketImporter {
    private static final Logger logger = LoggerFactory.getLogger(PhenopacketImporter.class);

    private final Phenopacket phenopacket;

    private final List<FenominalTerm> fenominalTermList;

    private final Timestamp createdOn;
    private final String createdBy;
    private final List<Update> updateList;

    private PhenopacketImporter(Phenopacket phenopacket, Ontology ontology) {
        this.phenopacket = phenopacket;
        this.fenominalTermList = new ArrayList<>();
        List<PhenotypicFeature> featureList = phenopacket.getPhenotypicFeaturesList();
        for (var feat : featureList) {
            boolean excluded = feat.getExcluded();
            boolean present = ! excluded;
            OntologyClass clz = feat.getType();
            String id = clz.getId();
            String label = clz.getLabel();
            TermId tid = TermId.of(id);
            if (! ontology.getTermMap().containsKey(tid)) {
                String errorStr = String.format("Could not find Ontology term for %s (%s) from phenopacket",
                        label, id);
                logger.error(errorStr);
                throw new PhenolRuntimeException(errorStr);
            }
            Term term = ontology.getTermMap().get(tid);
            PhenotypeTerm pterm = new PhenotypeTerm(term, present);
            if (feat.hasOnset()) {
                TimeElement onsetElem = feat.getOnset();
                if (onsetElem.hasAge()) {
                    Age age = onsetElem.getAge();
                    Period agePeriod = Period.parse(age.getIso8601Duration());
                    fenominalTermList.add(FenominalTerm.fromMainPhenotypeTermWithAge(pterm, agePeriod));
                } else {
                    String errorString = String.format("Age time element (%s) did not have iso8601 age", feat);
                    logger.error(errorString);
                    throw new PhenolRuntimeException(errorString);
                }
            } else {
                fenominalTermList.add(FenominalTerm.fromMainPhenotypeTerm(pterm));
            }
        }

        MetaData meta = phenopacket.getMetaData();
        this.createdOn = meta.getCreated();
        this.createdBy = meta.getCreatedBy();
        this.updateList = meta.getUpdatesList();
    }

    public Phenopacket getPhenopacket() {
        return phenopacket;
    }

    public List<FenominalTerm> getFenominalTermList() {
        return fenominalTermList;
    }

    public String getSex() {
        Individual subject = this.phenopacket.getSubject();
        return subject.getSex().name();
    }

    public String getId() {
        return phenopacket.getId();
    }

    public org.monarchinitiative.fenominal.gui.model.Sex sex() {
        if (phenopacket.hasSubject()) {
            Sex s = phenopacket.getSubject().getSex();
            return switch (s) {
                case UNKNOWN_SEX,UNRECOGNIZED -> org.monarchinitiative.fenominal.gui.model.Sex.UNKNOWN_SEX;
                case MALE -> org.monarchinitiative.fenominal.gui.model.Sex.MALE;
                case FEMALE -> org.monarchinitiative.fenominal.gui.model.Sex.FEMALE;
                case OTHER_SEX -> org.monarchinitiative.fenominal.gui.model.Sex.OTHER_SEX;
            };
        } else {
            return org.monarchinitiative.fenominal.gui.model.Sex.UNKNOWN_SEX;
        }
    }

    public Timestamp getCreatedOn() {
        return createdOn;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public List<SimpleUpdate> getUpdateList() {
        List<SimpleUpdate> updates = new ArrayList<>();
        for (Update update : updateList) {
            updates.add(new SimpleUpdate(update.getUpdatedBy(), update.getTimestamp()));
        }
        return updates;
    }

    /**
     * Factory method to obtain a Phenopacket object starting from a phenopacket in Json format
     *
     * @param phenopacketFile -- path to the phenopacket
     * @return {@link PhenopacketImporter} object corresponding to the PhenoPacket
     */
    public static PhenopacketImporter fromJson(File phenopacketFile, Ontology ontology) {
        JSONParser parser = new JSONParser();
        logger.trace("Importing Phenopacket: " + phenopacketFile);
        try {
            Object obj = parser.parse(new FileReader(phenopacketFile));
            JSONObject jsonObject = (JSONObject) obj;
            String phenopacketJsonString = jsonObject.toJSONString();
            Phenopacket.Builder phenoPacketBuilder = Phenopacket.newBuilder();
            JsonFormat.parser().merge(phenopacketJsonString, phenoPacketBuilder);
            return new PhenopacketImporter(phenoPacketBuilder.build(), ontology);
        } catch (IOException e1) {
            throw new FenominalRunTimeException("I/O Error: Could not load phenopacket  (" + phenopacketFile + "): " + e1.getMessage());
        } catch (ParseException ipbe) {
            System.err.println("[ERROR] Malformed phenopacket: " + ipbe.getMessage());
            throw new FenominalRunTimeException("Could not load phenopacket (" + phenopacketFile + "): " + ipbe.getMessage());
        }
    }











}
