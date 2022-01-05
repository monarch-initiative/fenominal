package org.monarchinitiative.fenominal.gui.questionnaire.phenoitem;

import org.monarchinitiative.phenol.ontology.data.Term;

/**
 * A kind of {@link PhenoItem} that does not have quantitative data associated with it
 * Therefore, all we show is a SegmentedButton
 */
public class SimplePhenoItem implements PhenoItem {
    private final Term hpoTerm;
    private AnswerType phenoanswer;


    public SimplePhenoItem(Term term) {
        this(term, AnswerType.UNKNOWN);
    }

    public SimplePhenoItem(Term term, AnswerType answerType) {
        this.hpoTerm = term;
        this.phenoanswer = answerType;
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
    public void updateAnswer(AnswerType answerType) {
        this.phenoanswer = answerType;
    }

    @Override
    public AnswerType answer() {
        return phenoanswer;
    }


    @Override
    public String toString() {
        return String.format("%s (%s): \"%s\"", term().getName(), term().getId().getValue(), answer());
    }


}
