package org.monarchinitiative.fenominal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class StockUiApplication {
    public static void main(String[] args) {
        Application.launch(ChartApplication.class, args);
    }
}

