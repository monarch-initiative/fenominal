package org.monarchinitiative.fenominal.gui.questionnaire.phenoitem;

public class AgeRule {


    private final PhenoAge thresholdAge;
    private final boolean youngerThanAbnormal;

    private AgeRule(PhenoAge thresholdAge, boolean younger){
        this.thresholdAge = thresholdAge;
        this.youngerThanAbnormal = younger;
    }

    /**
     * If {@link #youngerThanAbnormal} is true, then having the feature in question
     * at an age younger than {@link #thresholdAge} is abnormal (e.g., precocious puberty)
     * If {@link #youngerThanAbnormal} is false, then having the feature in question
     * at an age older than {@link #thresholdAge} is abnormal (e.g., learning to walk)
     * @param age The age of the individual
     * @return An {@link AnswerType} reflecting whether the finding is observed or included
     */
    public AnswerType interpret(PhenoAge age) {
        // if thresholdAge is older than age, c>0, if thresholdAge is younger than age, c<0
        // if they are equal, c==0
        int c = this.thresholdAge.compareTo(age);
        if (youngerThanAbnormal) {
            // having the feature at a younger age is abnormal
            // if c<=0, the threshold age is younger than the actual age, so the finding is normal
            if (c<=0) return  AnswerType.EXCLUDED;
            else return AnswerType.OBSERVED;
        } else {
            // having the feature at an older age is abnormal
            // if c==0, then the age is at the very upper range of normal
            if (c<0) return  AnswerType.OBSERVED;
            else return AnswerType.EXCLUDED;
        }
    }


    public static AgeRule youngerThanIsAbnormal(PhenoAge age) {
        return new AgeRule(age, true);
    }

    public static AgeRule olderThanIsAbnormal(PhenoAge age) {
        return new AgeRule(age, false);
    }

    @Override
    public String toString() {
        if (youngerThanAbnormal) {
            return "younger than " + thresholdAge;
        } else {
            return "older than " + thresholdAge;
        }
    }


}
