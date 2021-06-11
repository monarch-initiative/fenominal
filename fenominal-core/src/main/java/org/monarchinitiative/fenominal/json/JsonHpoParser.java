package org.monarchinitiative.fenominal.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.monarchinitiative.fenominal.except.FenominalRunTimeException;
import org.monarchinitiative.fenominal.json.model.GraphDocument;

import java.io.File;
import java.io.IOException;

public class JsonHpoParser {

    public JsonHpoParser(String hpoJsonPath) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            File f = new File(hpoJsonPath);
            GraphDocument gdoc = mapper.readValue(f, GraphDocument.class);
            System.out.println(gdoc.toString());
        } catch (IOException e) {
            throw new FenominalRunTimeException(e.getLocalizedMessage());
        }
    }

}
