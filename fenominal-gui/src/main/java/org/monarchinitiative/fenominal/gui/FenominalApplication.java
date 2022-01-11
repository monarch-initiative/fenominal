package org.monarchinitiative.fenominal.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;


public class FenominalApplication extends Application {
    private ConfigurableApplicationContext applicationContext;

    static public final String FENOMINAL_NAME_KEY = "fenominal.name";

    static public final String FENOMINAL_VERSION_PROP_KEY = "fenominal.version";


    @Override
    public void start(Stage stage) {
        applicationContext.publishEvent(new StageReadyEvent(stage));
    }

    @Override
    public void init() {
        applicationContext = new SpringApplicationBuilder(StockUiApplication.class).run();
        // export app's version into System properties
        try (InputStream is = FenominalApplication.class.getResourceAsStream("/application.properties")) {
            Properties properties = new Properties();
            properties.load(is);
            String version = properties.getProperty(FENOMINAL_VERSION_PROP_KEY, "unknown version");
            System.setProperty(FENOMINAL_VERSION_PROP_KEY, version);
            String name = properties.getProperty(FENOMINAL_NAME_KEY, "Fenominal");
            System.setProperty(FENOMINAL_NAME_KEY, name);
        } catch (IOException e) {
            e.printStackTrace();
        }
        File f = applicationContext.getBean("appHomeDir", File.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() throws Exception {
        // save properties
        final Properties pgProperties = applicationContext.getBean("pgProperties", Properties.class);
        final Path configFilePath = applicationContext.getBean("configFilePath", Path.class);
        try (OutputStream os = Files.newOutputStream(configFilePath)) {
            pgProperties.store(os, "Fenominal properties");
        }
        super.stop();
        applicationContext.close();
        Platform.exit();
    }


    static class StageReadyEvent extends ApplicationEvent {
        public StageReadyEvent(Stage stage) {
            super(stage);
        }

        public Stage getStage() {
            return ((Stage) getSource());
        }
    }

}
