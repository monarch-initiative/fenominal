package org.monarchinitiative.fenominal.gui.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ApplicationProperties {

    private final String applicationUiTitle;

    private final String applicationVersion;

    @Autowired
    public ApplicationProperties(@Value("${fenominal.name}") String uiTitle,
                                 @Value("${fenominal.version}") String version) {
        this.applicationUiTitle = uiTitle;
        this.applicationVersion = version;
    }



    public String getApplicationUiTitle() {
        return applicationUiTitle;
    }

    public String getApplicationVersion() {
        return applicationVersion;
    }
}