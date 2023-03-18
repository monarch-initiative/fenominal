package org.monarchinitiative.fenominal.core.impl.json.model.meta;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.monarchinitiative.fenominal.core.impl.json.model.Meta;

import java.util.List;

/**
 * <p>Associates the container object with a value via a property.</p>
 * <p>For example, a node representing an OWL class may contain a {@link Meta} object
 * containing a PropertyValue mapping to a textual definition string via a definition property.</p>
 * <p>Broadly, there are two categories of implementing class:
 * <ol>
 *     <li>PropertyValues corresponding to a specific explicitly modeled property type (e.g synonym)</li>
 *     <li>generic {@link BasicPropertyValue}s - anything property not explicitly modeled</li>
 * </ol>
 *  </p>
 * <p>A PropertyValue is minimally a tuple `(pred,value)`. However, each sub tuple may also
 * be "annotated" with additional metadata (this corresponds to an Axiom Annotation in OWL)
 * <ul>
 *     <li>Any tuple can be supported by an array of xrefs.</li>
 *     <li>Some implementing classes may choose to model additional explicit annotations (e.g. {@link SynonymPropertyValue})</li>
 * </ul>
 * @author cjm
 */
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public interface PropertyValue {

    /**
     * @return the meta
     */
    @JsonProperty
    Meta getMeta();

    /**
     * Predicates correspond to OWL properties. Like all preds in this datamodel,
     * a pred is represented as a String which denotes a CURIE
     *
     * @return the pred
     */
    @JsonProperty
    String getPred();

    /**
     * The value of the property-value
     *
     * @return the val
     */
    @JsonProperty
    String getVal();

    /**
     * An array denoting objects that support the property value assertion
     *
     * @return the xrefs
     */
    @JsonProperty
    List<String> getXrefs();

}
