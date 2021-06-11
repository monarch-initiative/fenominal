package org.monarchinitiative.fenominal.json;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.monarchinitiative.fenominal.json.model.GraphDocument;


import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Loads an ontology using the obograph library.
 *
 * @author Jules Jacobsen <j.jacobsen@qmul.ac.uk>
 */
public class OboGraphDocumentLoader {

  private OboGraphDocumentLoader() {
  }



  public static GraphDocument loadJson(Path path) throws IOException {
      return loadJson(Files.newInputStream(path));
  }

  public static GraphDocument loadJson(InputStream inputStream) throws IOException {
      ObjectMapper objectMapper = new ObjectMapper();
      return objectMapper.readValue(inputStream, GraphDocument.class);
  }

}
