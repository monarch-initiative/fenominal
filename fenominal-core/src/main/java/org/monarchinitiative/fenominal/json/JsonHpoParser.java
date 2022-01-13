package org.monarchinitiative.fenominal.json;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.annotation.JsonKey;
import org.monarchinitiative.fenominal.core.FenominalRunTimeException;
import org.monarchinitiative.fenominal.json.model.GraphDocument;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.prefixcommons.CurieUtil;

import java.io.File;
import java.io.IOException;

public class JsonHpoParser {

    private final Ontology hpo;

    public JsonHpoParser(String hpoJsonPath) {
        ObjectMapper mapper = new ObjectMapper();
        // skip fields not used in OBO such as domainRangeAxioms
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            File f = new File(hpoJsonPath);
            if (! f.isFile()) {
                throw new FenominalRunTimeException("Could not file hp.json file at " + f.getAbsolutePath());
            }
            GraphDocument gdoc = mapper.readValue(f, GraphDocument.class);
            //System.out.println(gdoc.toString());
            CurieUtil curieUtil =  CurieUtilBuilder.defaultCurieUtil();
            this.hpo = OntologyLoader.loadOntology(gdoc, curieUtil, "HP");

            } catch (IOException e) {
            throw new FenominalRunTimeException(e.getLocalizedMessage());
        }
    }

    public static Ontology loadOntology(String hpoJsonPath) {
        return loadOntology(new File(hpoJsonPath));
    }

    public static Ontology loadOntology(File hpoJsonPath) {
        ObjectMapper mapper = new ObjectMapper();
        // skip fields not used in OBO such as domainRangeAxioms
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            if (! hpoJsonPath.isFile()) {
                throw new FenominalRunTimeException("Could not file hp.json file at " + hpoJsonPath.getAbsolutePath());
            }
            GraphDocument gdoc = mapper.readValue(hpoJsonPath, GraphDocument.class);
            //System.out.println(gdoc.toString());
            CurieUtil curieUtil =  CurieUtilBuilder.defaultCurieUtil();
            return OntologyLoader.loadOntology(gdoc, curieUtil, "HP");

        } catch (IOException e) {
            throw new FenominalRunTimeException(e.getLocalizedMessage());
        }
    }

    public Ontology getHpo() {
        return hpo;
    }
}
