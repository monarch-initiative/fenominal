package org.monarchinitiative.fenominal.gui.questionnaire.phenoitem;

import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.ArrayList;
import java.util.List;

public class PhenoItemDemoGenerator {

    private final Ontology hpo;

    public PhenoItemDemoGenerator(Ontology ontology) {
        this.hpo = ontology;
    }

    public List<PhenoItem> generateItems() {
        List<PhenoItem> items = new ArrayList<>();
        Term arachnodactyly = getTerm("HP:0001166");
        items.add(new SimplePhenoItem(arachnodactyly));
        Term triangularFace = getTerm("HP:0000325");
        items.add(new SimplePhenoItem(triangularFace));
        Term delayedAbilityToWalk = getTerm("HP:0031936");
        PhenoAge eighteenMonths = new PhenoAge(1,6);
        AgeRule by18months = AgeRule.olderThanIsAbnormal(eighteenMonths);
        items.add(new AgeThresholdPhenoItem(delayedAbilityToWalk, by18months, "Age when first able to walk?"));
        Term delayedSit = getTerm("HP:0025336");
        PhenoAge nineMonths = new PhenoAge(0,9);
        AgeRule byNineMoneths = AgeRule.olderThanIsAbnormal(nineMonths);
        items.add(new AgeThresholdPhenoItem(delayedSit, byNineMoneths, "Age when first able to sit alone?"));
        Term headLag = getTerm("HP:0032988");
        PhenoAge fourMonths = new PhenoAge(0,4);
        AgeRule byFourMonths = AgeRule.olderThanIsAbnormal(fourMonths);
        items.add(new AgeThresholdPhenoItem(headLag, byFourMonths, "Latest Age when head lag still observed?"));
        Term rollOver = getTerm("HP:0032989");
        PhenoAge sixMonths = new PhenoAge(0,6);
        AgeRule bySixMonths = AgeRule.olderThanIsAbnormal(sixMonths);
        items.add(new AgeThresholdPhenoItem(rollOver, bySixMonths, "Age when first able to roll front to back/back to front.?"));
        return items;
    }

    private Term getTerm(String id) {
        TermId tid = TermId.of(id);
        return hpo.getTermMap().get(tid);
    }



}
