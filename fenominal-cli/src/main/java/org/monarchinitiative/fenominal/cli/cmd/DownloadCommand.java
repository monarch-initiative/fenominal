package org.monarchinitiative.fenominal.cli.cmd;

import org.monarchinitiative.biodownload.BioDownloader;
import org.monarchinitiative.biodownload.BioDownloaderBuilder;
import org.monarchinitiative.biodownload.FileDownloadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Download a number of files needed for the analysis. We download by default to a subdirectory called
 * {@code data}, which is created if necessary. We download the files {@code hp.obo}, {@code phenotype.hpoa},
 * {@code Homo_sapiencs_gene_info.gz}, and {@code mim2gene_medgen}.
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 */

@CommandLine.Command(name = "download", aliases = {"D"},
        mixinStandardHelpOptions = true,
        description = "Download files for fenominal")
public class DownloadCommand implements Callable<Integer>{
    private static final Logger logger = LoggerFactory.getLogger(DownloadCommand.class);
    @CommandLine.Option(names={"-d","--data"}, description ="directory to download data (default: ${DEFAULT-VALUE})" )
    public String datadir="data";

    @CommandLine.Option(names={"-w","--overwrite"}, description = "overwrite previously downloaded files (default: ${DEFAULT-VALUE})")
    public boolean overwrite;

    @Override
    public Integer call() {
        logger.info(String.format("Download analysis to %s", datadir));
        Path destination = Paths.get(datadir);
        BioDownloaderBuilder builder = BioDownloader.builder(destination);
        BioDownloader downloader = builder.hpoJson().overwrite(overwrite).build();
        try {
            List<File> files = downloader.download();
            for (File f : files) {
                System.out.printf("[INFO] Downloaded %s\n", f.getAbsolutePath());
            }
        } catch (FileDownloadException e) {
            System.err.printf("[ERROR] Could not download hp.json: %s\n", e.getMessage());
        }
        return 0;
    }

}
