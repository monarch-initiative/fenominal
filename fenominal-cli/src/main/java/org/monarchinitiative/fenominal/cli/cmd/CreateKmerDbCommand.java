package org.monarchinitiative.fenominal.cli.cmd;


import org.monarchinitiative.fenominal.core.kmer.KmerGenerator;
import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import picocli.CommandLine;

import java.io.File;
import java.util.concurrent.Callable;


/**
 * Create kmer db files for testing purposes. Note that this goes so fast that we will
 * probably remove this feature from the final app and do everything in memory.
 */
@CommandLine.Command(name = "kmer", aliases = {"K"},
        mixinStandardHelpOptions = true,
        description = "Create kmer DB files")
public class CreateKmerDbCommand implements Callable<Integer> {
    @CommandLine.Option(names={"-d","--data"}, description ="directory to create kmer file (default: ${DEFAULT-VALUE})" )
    public String datadir = "data";

    @CommandLine.Option(names={"-k","--kmer-size"}, description ="kmer size (default: ${DEFAULT-VALUE})" )
    public int kmer_k = 5;

    public CreateKmerDbCommand(){
    }
    @Override
    public Integer call() throws Exception {
        File hpJson = new File(datadir + File.separator + "hp.json");
        if (! hpJson.isFile()) {
            System.err.printf("[ERROR] Could not find hp.sjon file at %s\nRun Download command first.\n", hpJson);
            return 1;
        }
        Ontology hp = OntologyLoader.loadOntology(hpJson);
        KmerGenerator kmerGenerator = new KmerGenerator(hp);
        kmerGenerator.doKMers(kmer_k);
        String outfilename = datadir + File.separator + "kmer" + kmer_k + ".ser";
        kmerGenerator.serialize(outfilename);
        return 0;
    }
}
