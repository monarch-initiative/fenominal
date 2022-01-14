package org.monarchinitiative.fenominal.gui.model;

import com.google.protobuf.Timestamp;

public record SimpleUpdate(String createdBy, Timestamp createdOn) {


    public static SimpleUpdate of(String createdBy, Timestamp createdOn) {
        return new SimpleUpdate(createdBy, createdOn);
    }
}
