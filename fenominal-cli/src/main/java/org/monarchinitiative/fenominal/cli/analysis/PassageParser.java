package org.monarchinitiative.fenominal.cli.analysis;

import org.monarchinitiative.fenominal.core.TextToHpoMapper;
import org.monarchinitiative.fenominal.core.corenlp.MappedSentencePart;
import org.monarchinitiative.fenominal.core.hpo.HpoLoader;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.List;

public abstract class PassageParser {
    private final String hpoJsonPath;
    private final TextToHpoMapper mapper;
    protected final Ontology ontology;


    protected final String output;


    public PassageParser(String hpoJsonPath, String output) {
        this.hpoJsonPath = hpoJsonPath;
        this.output = output;
        HpoLoader hpoLoader = new HpoLoader(hpoJsonPath);
        this.ontology = hpoLoader.getHpo();
        this.mapper = new TextToHpoMapper(this.ontology);
    }


    protected List<MappedSentencePart> getMappedSentenceParts (String content) {
        return mapper.mapText(content);
    }


    abstract public void parse();
}
