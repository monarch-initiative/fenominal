package org.monarchinitiative.fenominal.gui.questionnaire.qtable;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

public class AgeBox extends HBox {
    private final Spinner<Integer> years;
    private final Spinner<Integer> months;
    private final Spinner<Integer> days;

    private final IntegerProperty yearsProperty;
    private final IntegerProperty monthsProperty;
    private final IntegerProperty daysProperty;

    public AgeBox() {
        Font font = Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 12);
        Label yLab = new Label("Y ");
        yLab.setFont(font);
        Label mLab = new Label("M ");
        mLab.setFont(font);
        Label dLab = new Label("D ");
        dLab.setFont(font);
        this.years = new Spinner<>(0, 110, 0);
        years.setEditable(true);
        years.setPrefSize(75, 25);
        this.months = new Spinner<>(0, 11, 0);
        months.setEditable(true);
        months.setPrefSize(75, 25);
        this.days = new Spinner<>(0, 30, 0);
        days.setEditable(true);
        days.setPrefSize(75, 25);
        setPadding(new Insets(10, 10, 10, 25));
        getChildren().addAll(yLab, years, mLab, months, dLab, days);
        yearsProperty = new SimpleIntegerProperty();
        yearsProperty.bind(years.valueProperty());
        monthsProperty = new SimpleIntegerProperty();
        monthsProperty.bind(months.valueProperty());
        daysProperty = new SimpleIntegerProperty();
        daysProperty.bind(days.valueProperty());
    }

    public int getYearsProperty() {
        return yearsProperty.get();
    }

    public IntegerProperty yearsPropertyProperty() {
        return yearsProperty;
    }

    public int getMonthsProperty() {
        return monthsProperty.get();
    }

    public IntegerProperty monthsPropertyProperty() {
        return monthsProperty;
    }

    public int getDaysProperty() {
        return daysProperty.get();
    }

    public IntegerProperty daysPropertyProperty() {
        return daysProperty;
    }

    public Spinner<Integer> getYearsSpinner() {
        return years;
    }

    public Spinner<Integer> getMonthsSpinner() {
        return months;
    }

    public Spinner<Integer> getDaysSpinner() {
        return days;
    }
}
