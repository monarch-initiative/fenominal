package org.monarchinitiative.fenominal.core.impl.json.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

/**
 * A graph object holds a collection of nodes and edges.
 * Corresponds to a Named Graph in RDF, and an Ontology in OWL
 * <p></p>
 * Note: there is no assumption that either nodes or edges are unique to a graph
 * ## Basic OBO Graphs
 * @author cjm
 *
 */
@JsonDeserialize(builder = Graph.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class Graph {

    private Graph(Builder builder) {
        id = builder.id;
        lbl = builder.lbl;
        meta = builder.meta;
        nodes = builder.nodes;
        edges = builder.edges;
    }

    private final List<Node> nodes;
    private final List<Edge> edges;
    private final String id;
    private final String lbl;
    private final Meta meta;


    /**
     * @return the nodes
     */
    public List<Node> getNodes() {
        return nodes;
    }



    /**
     * @return the edges
     */
    public List<Edge> getEdges() {
        return edges;
    }



    /**
     * @return the id
     */
    public String getId() {
        return id;
    }



    /**
     * @return the lbl
     */
    public String getLbl() {
        return lbl;
    }



    /**
     * @return the meta
     */
    public Meta getMeta() {
        return meta;
    }

    @Override
    public String toString() {
        return "Graph{" +
                "nodes=" + nodes +
                ", edges=" + edges +
                ", id='" + id + '\'' +
                ", lbl='" + lbl + '\'' +
                ", meta=" + meta +
                '}';
    }

    public static class Builder {

        @JsonProperty
        private String id;
        @JsonProperty
        private String lbl;
        @JsonProperty
        private Meta meta;
        @JsonProperty
        private List<Node> nodes;
        @JsonProperty
        private List<Edge> edges;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder lbl(String lbl) {
            this.lbl = lbl;
            return this;
        }

        public Builder meta(Meta meta) {
            this.meta = meta;
            return this;
        }

        // TODO: test for uniqueness
        public Builder nodes(List<Node> nodes) {
            this.nodes = nodes;
            return this;
        }
        public Builder edges(List<Edge> edges) {
            this.edges = edges;
            return this;
        }
        @JsonCreator
        public Graph build() {
            return new Graph(this);
        }
    }

}
