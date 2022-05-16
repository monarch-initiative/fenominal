package org.monarchinitiative.fenominal.gui.model;

import org.monarchinitiative.fenominal.core.FenominalRunTimeException;

/**
 * POJO for returning data about a publication that we would like to curate.
 */
public record CohortPublicationData(String pmid, String omimId, String diseasename) {


    public String getPmid() {
        if (this.pmid.startsWith("PMID")) {
            return this.pmid.replaceAll(" ", "");
        } else {
            throw new FenominalRunTimeException("Malformed PMID (" + pmid +"). PMIDs must be entered as PMID:123");
        }
    }

    public String getOmimId() {
        if (this.omimId.length() == 6) {
            // assume user has left out the OMIM:
            try {
                Integer o = Integer.parseInt(this.omimId);
                return "OMIM:" + omimId;
            } catch (NumberFormatException e) {
                throw new FenominalRunTimeException("Malformed OMIM id (" + this.omimId + ")");
            }
        } else if (this.omimId.startsWith("OMIM:")) {
            return this.omimId.replaceAll(" ", "");
        } else {
            throw new FenominalRunTimeException("Malformed OMIM id (" + this.omimId + ")");
        }
    }

}
