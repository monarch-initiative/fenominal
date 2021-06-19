package org.monarchinitiative.fenominal.gui.model;

import org.monarchinitiative.hpotextmining.gui.controller.Main;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CaseReport implements TextMiningResultsModel {

    private List<FenominalTerm> terms;

    public CaseReport(Set<Main.PhenotypeTerm> terms) {
        List<FenominalTerm> list = terms.stream().map(FenominalTerm::fromMainPhenotypeTerm).sorted().collect(Collectors.toList());
        this.terms = List.copyOf(list);
    }


    @Override
    public void output() {
        for (var mt : terms) {
            System.out.println(mt.toString());
        }
    }
}
