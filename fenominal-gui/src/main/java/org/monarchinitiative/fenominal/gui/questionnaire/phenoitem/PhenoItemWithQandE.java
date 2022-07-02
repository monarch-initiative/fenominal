package org.monarchinitiative.fenominal.gui.questionnaire.phenoitem;

import org.monarchinitiative.phenol.ontology.data.Term;

public class PhenoItemWithQandE implements PhenoItem {

    private final Term hpoTerm;
    private AnswerType phenoanswer;
    private final String question;
    private final String explanation;

    public PhenoItemWithQandE(Term term, String qu, String expl) {
        this.hpoTerm = term;
        this.phenoanswer = AnswerType.UNKNOWN;
        this.question = qu;
        this.explanation = expl;
    }

    @Override
    public String termLabel() {
        return hpoTerm.getName();
    }

    @Override
    public Term term() {
        return this.hpoTerm;
    }

    @Override
    public AnswerType answer() {
        return this.phenoanswer;
    }

    @Override
    public String question(){ return this.question; }

    @Override
    public String explanation() {
        return explanation;
    }

    @Override
    public void updateAnswer(AnswerType answer) {
        this.phenoanswer = answer;
    }


    @Override
    public String toString() {
        return String.format("%s (%s): \"%s\" - (Question %s)", term().getName(), term().id().getValue(),
                answer(), this.question);
    }
}
