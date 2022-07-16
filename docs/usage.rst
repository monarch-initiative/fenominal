.. _rstusage:

fenominal library
=================

fenominal is a modular library. To use fenominal in applications, first initialize a ``TermMiner`` object.
Fenominal offers an "exact" mapper and a prototype "fuzzy" matcher.

.. code-block:: java
   :caption: initializing the TermMiner

    import org.monarchinitiative.fenominal.core.FenominalRunTimeException;
    import org.monarchinitiative.fenominal.core.TermMiner;
    import org.monarchinitiative.fenominal.model.MinedSentence;
    import org.monarchinitiative.fenominal.model.MinedTermWithMetadata;
    import org.monarchinitiative.phenol.io.OntologyLoader;
    import org.monarchinitiative.phenol.ontology.data.Ontology;
    import org.monarchinitiative.phenol.ontology.data.TermId;

    String hpoJsonFile = "/some/path/hpjson";
    Ontology ontology = OntologyLoader.loadOntology(new File(hpoJsonPath));
    TermMiner miner =  TermMiner.defaultNonFuzzyMapper(this.ontology);

To initialize a fuzzy mapper, change the last line to this.

.. code-block:: java
   :caption: initializing the fuzzy TermMiner

    TermMiner miner  =  TermMiner.defaultFuzzyMapper(this.ontology);


Performing text mining
######################

fenominal offers three ways to retrieve mined ontology concepts. This is still an experimental feature and
the details of the interface are likely to change. Both the exact and the fuzzy match can provide the
same interfaces to results.

MinedTerm
^^^^^^^^^

This is the simplest way to retrieve results. MinedTerm contains a minimum amount of information about
the indentified concept.


.. code-block:: java
   :caption: Retrieving MinedTerm objects

    String content = "text you want to mine";
    Collection<MinedTerm> minedTerms = miner.mineTerms(content);
    for (MinedTerm mt : minedTerms) {
      // do something
    }

The ``MinedTerm`` interfaces defines the following accessors. The coordinates are Java-style (zero-based, open-ended).

.. code-block:: java
   :caption: MinedTerm interface

    int getBegin();
    int getEnd();
    String getTermIdAsString();
    boolean isPresent();

MinedTermWithMetadata
^^^^^^^^^^^^^^^^^^^^^

The ``MinedTermWithMetadata`` interface provides more information about the mined concept than ``MinedTerm`` does.


.. code-block:: java
   :caption: Retrieving MinedTermWithMetadata objects

    String content = "text you want to mine";
    Collection<MinedTermWithMetadata> minedTermsWithMetadata = miner.mineTermsWithMetadata(content);
    for (MinedTermWithMetadata mtwmd : minedTermsWithMetadata) {
      // do something
    }

The ``MinedTermWithMetadata`` interfaces defines the following accessors. The coordinates are Java-style (zero-based, open-ended).

.. code-block:: java
   :caption: MinedTerm interface

    int getBegin();
    int getEnd();
    String getTermIdAsString();
    boolean isPresent();
    String getMatchingString();
    double getSimilarity();
    TermId getTermId();
    int getTokenCount();

MinedSentence
^^^^^^^^^^^^^

fenominal works sentence by sentence. The interface is designed to group the mined concepts by sentence.


.. code-block:: java
   :caption: Retrieving MinedTermWithMetadata objects

    String content = "text you want to mine";
    Collection<MinedSentence> minedSentences = miner.mineSentences(content);
    for (MinedSentence sentence : minedSentences) {
      // do something
    }

The ``MinedSentence`` interfaces defines the following accessors. ``getText`` retrieves the original sentence.

.. code-block:: java
   :caption: MinedSentence interface

    Collection<MinedTermWithMetadata> getMinedTerms();
    String getText();