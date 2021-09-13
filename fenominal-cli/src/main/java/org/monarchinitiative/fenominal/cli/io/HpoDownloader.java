package org.monarchinitiative.fenominal.cli.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Command to download the {@code hp.obo} and {@code phenotype.hpoa} files that
 * we will need to run the Fenominal approach.
 *
 * @author Peter N Robinson
 */
public class HpoDownloader {

    private static final Logger logger = LoggerFactory.getLogger(HpoDownloader.class);

    /**
     * Directory to which we will download the files.
     */
    private final String downloadDirectory;

    /**
     * If true, download new version whether or not the file is already present.
     */
    private final boolean overwrite;

    private final static String HP_JSON = "hp.json";

    /**
     * URL of the hp.obo file.
     */
    private final static String HP_JSON_URL = "https://raw.githubusercontent.com/obophenotype/human-phenotype-ontology/master/hp.json";

    public HpoDownloader(String path, boolean overwrite) {
        this.downloadDirectory = path;
        this.overwrite = overwrite;
        logger.info("overwrite: {}", overwrite);
    }

    /**
     * Download the files unless they are already present.
     */
    public void download() {
        downloadFileIfNeeded(HP_JSON, HP_JSON_URL);
        logger.info("Downloaded hp.obo to `{}`", downloadDirectory);
    }


    private void downloadFileIfNeeded(String filename, String webAddress) {
        File f = new File(downloadDirectory, filename);
        if (f.exists() && (!overwrite)) {
            logger.trace("Cowardly refusing to download {} since we found it at {}", filename, f.getAbsolutePath());
            return;
        }
        FileDownloader downloader = new FileDownloader();
        try {
            URL url = new URL(webAddress);
            logger.debug("Created url from " + webAddress + ": " + url);
            downloader.copyURLToFile(url, new File(f.getAbsolutePath()));
        } catch (MalformedURLException e) {
            logger.error("Malformed URL for {} [{}]: {}", filename, webAddress, e.getMessage());
        } catch (FileDownloadException e) {
            logger.error("Error downloading {} from {}: {}", filename, webAddress, e.getMessage());
        }
        logger.info("Downloaded {}", filename);
    }

}
