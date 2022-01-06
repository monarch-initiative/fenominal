package org.monarchinitiative.fenominal.gui.questionnaire.phenoitem;

import org.monarchinitiative.phenol.ontology.data.Term;

import java.util.Optional;

public class AgeThresholdPhenoItem implements PhenoItem {

    private final Term hpoTerm;
    private AnswerType phenoanswer;
    private final AgeRule ageRule;
    private PhenoAge age;
    private final String question;

    public AgeThresholdPhenoItem(Term term, AgeRule ageRule, String qu) {
        this.hpoTerm = term;
        this.ageRule = ageRule;
        this.phenoanswer = AnswerType.UNKNOWN;
        this.question = qu;
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
    public Optional<AgeRule> ageRuleOpt() {
        return Optional.of(ageRule);
    }

    @Override
    public AnswerType answer() {
        return this.phenoanswer;
    }

    @Override
    public String question(){ return this.question; }

    @Override
    public void updateAnswer(AnswerType answer) {
        this.phenoanswer = answer;
    }

    @Override
    public void updateAge(PhenoAge age) {
        this.age = age;
        if (age.initialized()) {
            this.phenoanswer = ageRule.interpret(age);
        }
    }

    @Override
    public Optional<PhenoAge> age() {
        return Optional.ofNullable(this.age);
    }


    @Override
    public String toString() {
        return String.format("%s (%s): \"%s\" - Threshold %s/Observed %s", term().getName(), term().getId().getValue(),
                answer(), this.ageRule, this.age);
    }
}
