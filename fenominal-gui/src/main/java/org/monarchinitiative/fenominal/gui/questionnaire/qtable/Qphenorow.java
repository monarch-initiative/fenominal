package org.monarchinitiative.fenominal.gui.questionnaire.qtable;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.monarchinitiative.fenominal.gui.questionnaire.phenoitem.*;
import org.monarchinitiative.phenol.ontology.data.Term;

import java.util.Optional;

public class Qphenorow {

    private final StringProperty title;

    private final StringProperty question;

    TermSelectionButton termSelectionButton;

    AgeBox ageBox;

    SimpleIntegerProperty years;

    SimpleIntegerProperty months;

    SimpleIntegerProperty days;


    private final PhenoItem phenoItem;



    public Qphenorow(PhenoItem item) {
        this.phenoItem = item;
        this.title = new SimpleStringProperty();
        this.question = new SimpleStringProperty();
        this.years = new SimpleIntegerProperty();
        this.months = new SimpleIntegerProperty();
        this.days = new SimpleIntegerProperty();
        this.termSelectionButton = new TermSelectionButton();
        this.ageBox = new AgeBox();
        setTitle(item.termLabel());
        setQuestion(item.question());
    }
    public void setTermSelectionButton(TermSelectionButton termSelectionButton) {
        this.termSelectionButton = termSelectionButton;
    }

    public Term getHpoTerm() {
        return phenoItem.term();
    }

    public void updateAnswer() {
        AnswerType answer = termSelectionButton.getAnswer();
       phenoItem.updateAnswer(answer);
    }

   public String getExplanation() { return phenoItem.explanation(); }

    public String getTitle() {
        return title.get();
    }

    public StringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public int getYears() {
        return years.get();
    }

    public IntegerProperty yearsProperty() {
        return years;
    }

    public void setYears(int years) {
        this.years.set(years);
    }

    public int getMonths() {
        return months.get();
    }

    public IntegerProperty monthsProperty() {
        return months;
    }

    public void setMonths(int months) {
        this.months.set(months);
    }

    public int getDays() {
        return days.get();
    }

    public IntegerProperty daysProperty() {
        return days;
    }

    public void setDays(int days) {
        this.days.set(days);
    }

    /**
     * Returns the Phenoitem that corresponds to the choice of the user.
     * If this is an AgeRule phenoitem, we apply the rule to decide upon the appropriate call for the HPO term
     * @return the PhenoItem on which the row is based
     */
    public PhenoItem toPhenoItem() {
        return phenoItem;
    }

    public String getQuestion() {
        return question.get();
    }

    public StringProperty questionProperty() {
        return question;
    }

    public TermSelectionButton getTermSelectionButton() {
        return termSelectionButton;
    }

    public void setQuestion(String question) {
        this.question.set(question);
    }

    public void reset() {
        this.phenoItem.updateAnswer(AnswerType.UNKNOWN);
    }


    public PhenoAnswer phenoAnswer() {
        return new DefaultPhenoAnswer(phenoItem);
    }
}
