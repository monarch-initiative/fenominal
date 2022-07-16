package org.monarchinitiative.fenominal.core.impl;

import org.monarchinitiative.fenominal.core.FenominalRunTimeException;
import org.monarchinitiative.fenominal.core.TermMiner;
import org.monarchinitiative.fenominal.core.impl.kmer.KmerGenerator;
import org.monarchinitiative.phenol.ontology.data.Ontology;

import java.io.File;
import java.io.IOException;

public abstract class AbstractTermMiner implements TermMiner {





    @Override
    public void serializeKmersToFile(Ontology ontology, File file, int k) {
        if (!file.isFile()) {
            throw new FenominalRunTimeException("Could not find file to write kmer serialized data at "
                    + file.getAbsolutePath());
        }
        KmerGenerator kmerGenerator = new KmerGenerator(ontology);
        kmerGenerator.doKMers(k);
        try {
            kmerGenerator.serialize(file);
        } catch (IOException e) {
            throw new FenominalRunTimeException("Could not serialize kmer  data:"
                    + e.getMessage());
        }
    }
}
