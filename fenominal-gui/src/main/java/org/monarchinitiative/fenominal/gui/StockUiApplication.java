package org.monarchinitiative.fenominal.gui;

import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * Main class of the fenominal GUI app
 * @author Peter N Robinson
 */
@SpringBootApplication
public class StockUiApplication {
    public static void main(String[] args) {
        Application.launch(FenominalApplication.class, args);
    }
}

