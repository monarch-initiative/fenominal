package org.monarchinitiative.fenominal.json;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.monarchinitiative.fenominal.except.FenominalRunTimeException;
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
            GraphDocument gdoc = mapper.readValue(f, GraphDocument.class);
            System.out.println(gdoc.toString());
            CurieUtil curieUtil =  CurieUtilBuilder.defaultCurieUtil();
            this.hpo = OntologyLoader.loadOntology(gdoc, curieUtil, "HP");

            } catch (IOException e) {
            throw new FenominalRunTimeException(e.getLocalizedMessage());
        }
    }

    public Ontology getHpo() {
        return hpo;
    }
}
