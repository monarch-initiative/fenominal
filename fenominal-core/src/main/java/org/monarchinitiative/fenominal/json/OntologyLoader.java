package org.monarchinitiative.fenominal.json;

import com.google.common.collect.ImmutableSet;

import org.monarchinitiative.fenominal.json.model.GraphDocument;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.prefixcommons.CurieUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * Entry class for loading an ontology from a File or InputStream. Files can be in OWL, OBO or JSON format and will be
 * handled transparently.
 * <p>
 * The default for loading from a file is that all terms will be loaded into the graph. For simpler ontologies with only
 * one namespace e.g. HPO it is safe to load the ontology without supplying any termId prefixes. For the GO, which
 * contains a mixture of GO, RO and BFO terms it is advisable to supply the 'GO' termId prefix otherwise there may be
 * relationships with RelationshipType.UNKNOWN. It is left to the user how best to specify what is loaded.
 *
 * @author Jules Jacobsen <j.jacobsen@qmul.ac.uk>
 */
public class OntologyLoader {

  private static final Logger logger = LoggerFactory.getLogger(OntologyLoader.class);

  private OntologyLoader() {
  }



  public static Ontology loadOntology(GraphDocument graphDocument, CurieUtil curieUtil, String... termIdPrefixes) {
    logger.debug("Finished loading ontology");
    logger.debug("Creating phenol ontology");
    OboGraphDocumentAdaptor graphDocumentAdaptor = OboGraphDocumentAdaptor.builder()
      .curieUtil(curieUtil)
      .wantedTermIdPrefixes(ImmutableSet.copyOf(termIdPrefixes))
      .build(graphDocument);

    Ontology ontology = graphDocumentAdaptor.buildOntology();
    logger.debug("Parsed a total of {} terms", ontology.countAllTerms());
    return ontology;
  }





  private static String readBytes(InputStream bufferedStream, int readlimit) throws IOException {
    byte[] firstFewBytes = new byte[readlimit];
    if (bufferedStream.read(firstFewBytes) == readlimit) {
      return new String(firstFewBytes);
    }
    return null;
  }

  private static boolean isJsonGraphDoc(String firstBytes) {
    return firstBytes != null && firstBytes.replace("\\W+", "").startsWith("{");
  }
}
