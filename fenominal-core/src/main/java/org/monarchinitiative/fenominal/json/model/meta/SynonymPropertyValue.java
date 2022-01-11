package org.monarchinitiative.fenominal.json.model.meta;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.monarchinitiative.fenominal.json.model.Meta;

import java.util.Collections;
import java.util.List;


/**
 * A {@link PropertyValue} that represents a an alternative term for a node
 *
 * @author cjm
 */
@JsonDeserialize(builder = SynonymPropertyValue.Builder.class)
public class SynonymPropertyValue extends AbstractPropertyValue {

    private final String synonymType;

    /**
     * @return the synonymType
     */
    public String getSynonymType() {
        return synonymType;
    }

    /**
     * OBO-style synonym scopes
     *
     * @author cjm
     */
    public enum SCOPES {
        EXACT,
        NARROW,
        BROAD,
        RELATED
    }

    /**
     * properties from oboInOwl vocabulary that represent scopes
     *
     * @author cjm
     */
    public enum PREDS {
        hasExactSynonym,
        hasNarrowSynonym,
        hasBroadSynonym,
        hasRelatedSynonym
    }

    private SynonymPropertyValue(Builder builder) {
        super(builder);
        synonymType = builder.synonymType;
    }

    /**
     * @return true is scope equals EXACT -- convenience predicate
     */
    @JsonIgnore
    public boolean isExact() {
        return getPred().equals(PREDS.hasExactSynonym.toString());
    }

    @JsonIgnore
    public List<String> getTypes() {
        if (getMeta() != null) {
            return getMeta().getSubsets();
        }
        return Collections.emptyList();
    }

    public static class Builder extends AbstractPropertyValue.Builder {

        @JsonProperty
        private String synonymType;

        @Override
        public Builder val(String val) {
            return (Builder) super.val(val);
        }

        @Override
        public Builder xrefs(List<String> xrefs) {
            return (Builder) super.xrefs(xrefs);
        }

        public Builder addType(String type) {
            // TODO: decide on pattern for nested builders
            super.meta(new Meta.Builder().subsets(Collections.singletonList(type)).build());
            return this;
        }

        public Builder scope(SCOPES scope) {
            PREDS pred = switch (scope) {
                case EXACT -> PREDS.hasExactSynonym;
                case RELATED -> PREDS.hasRelatedSynonym;
                case BROAD -> PREDS.hasBroadSynonym;
                case NARROW -> PREDS.hasNarrowSynonym;
            };
            super.pred(pred.toString());
            return this;
        }

        public Builder synonymType(String synonymType) {
            if (synonymType != null)
                this.synonymType = synonymType;
            return this;
        }

        @JsonCreator
        public SynonymPropertyValue build() {
            return new SynonymPropertyValue(this);
        }
    }

}
