package org.monarchinitiative.fenominal.gui.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;

@Configuration
public class FenominalConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(FenominalConfig.class);


    @Bean
    public File appHomeDir() throws IOException {
        String osName = System.getProperty("os.name").toLowerCase();
        File appHomeDir;
        if (osName.contains("nix") || osName.contains("nux") || osName.contains("aix")) { // Unix
            appHomeDir = new File(System.getProperty("user.home") + File.separator + ".fenominal");
        } else if (osName.contains("win")) { // Windows
            appHomeDir = new File(System.getProperty("user.home") + File.separator + "fenominal");
        } else if (osName.contains("mac")) { // OsX
            appHomeDir = new File(System.getProperty("user.home") + File.separator + ".fenominal");
        } else { // unknown platform
            appHomeDir = new File(System.getProperty("user.home") + File.separator + "fenominal");
        }

        if (!appHomeDir.exists()) {
            LOGGER.debug("App home directory does not exist at {}", appHomeDir.getAbsolutePath());
            if (!appHomeDir.getParentFile().exists() && !appHomeDir.getParentFile().mkdirs()) {
                LOGGER.warn("Unable to create parent directory for app home at {}",
                        appHomeDir.getParentFile().getAbsolutePath());
                throw new IOException("Unable to create parent directory for app home at " +
                        appHomeDir.getParentFile().getAbsolutePath());
            } else {
                if (!appHomeDir.mkdir()) {
                    LOGGER.warn("Unable to create app home directory at {}", appHomeDir.getAbsolutePath());
                    throw new IOException("Unable to create app home directory at " + appHomeDir.getAbsolutePath());
                } else {
                    LOGGER.info("Created app home directory at {}", appHomeDir.getAbsolutePath());
                }
            }
        }
        return appHomeDir;
    }


//    @Bean("appNameVersion")
//    String appNameVersion(String appVersion, String appName) {
//        return String.format("%s : %s", appName, appVersion);
//    }
//
//
//    @Bean("appVersion")
//    String appVersion() {
//        // this property is set in FenominalApplication#init()
//        return System.getProperty(FenominalApplication.FENOMINAL_VERSION_PROP_KEY);
//    }
//
//    @Bean("appName")
//    String appName() {
//        // this property is set in FenominalApplication#init()
//        return System.getProperty(FenominalApplication.FENOMINAL_NAME_KEY);
//    }
}
