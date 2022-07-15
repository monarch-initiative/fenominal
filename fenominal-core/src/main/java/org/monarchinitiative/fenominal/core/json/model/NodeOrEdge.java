package org.monarchinitiative.fenominal.core.json.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public interface NodeOrEdge {

    @JsonProperty
    Meta getMeta();
}
