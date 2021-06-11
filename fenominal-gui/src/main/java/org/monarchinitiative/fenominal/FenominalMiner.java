package org.monarchinitiative.fenominal;

import org.monarchinitiative.fenominal.corenlp.MappedSentencePart;
import org.monarchinitiative.fenominal.textmapper.ClinicalTextMapper;
import org.monarchinitiative.hpotextmining.core.miners.MinedTerm;
import  org.monarchinitiative.hpotextmining.core.miners.TermMiner;
import org.monarchinitiative.phenol.ontology.data.Ontology;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class FenominalMiner implements TermMiner {
    private final ClinicalTextMapper mapper;



    public FenominalMiner(String hpOboPath) {
        this.mapper = new ClinicalTextMapper(hpOboPath);
    }

    /**
     * TODO -- currently we do not have excluded/negated terms and this function needs to be extended.
     * @param query
     * @return
     */
    @Override
    public Collection<MinedTerm> doMining(final String query) {
        List<MappedSentencePart> mappedSentenceParts = mapper.mapText(query);
        return mappedSentenceParts.stream().map(SimpleMinedTerm::fromMappedSentencePart).collect(Collectors.toList());
    }

    public Ontology getHpo() {
        return this.mapper.getHpo();
    }


}
