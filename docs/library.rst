.. _rstlibrary:

=================
fenominal library
=================


To use fenominal as a library for Java 17 or higher applications, add the following to the POM file.

.. code-block:: console

    <dependency>
        <groupId>org.monarchinitiative.fenominal</groupId>
        <artifactId>fenominal-core</artifactId>
        <version>...</version>
    </dependency>
    <dependency>
        <groupId>org.monarchinitiative.phenol</groupId>
        <artifactId>phenol-core</artifactId>
        <version>...</version>
    </dependency>
    <dependency>
        <groupId>org.monarchinitiative.phenol</groupId>
        <artifactId>phenol-io</artifactId>
        <version>...</version>
    </dependency>


Using the latest versions of fenominal and phenol.

TODO explain how to use GRAIL.

Imports
^^^^^^^

Use the following imports

.. code-block:: java

    import org.monarchinitiative.fenominal.core.FenominalRunTimeException;
    import org.monarchinitiative.fenominal.core.TermMiner;
    import org.monarchinitiative.fenominal.model.MinedSentence;
    import org.monarchinitiative.fenominal.model.MinedTermWithMetadata;
    import org.monarchinitiative.phenol.io.OntologyLoader;
    import org.monarchinitiative.phenol.ontology.data.Ontology;
    import org.monarchinitiative.phenol.ontology.data.TermId;


Initialize with the path to the hp.json file

.. code-block:: java

    Ontology ontology = OntologyLoader.loadOntology(new File(hpoJsonPath));

Decide whether to do exact or T-BLAT (fuzzy) matching

.. code-block:: java

    boolean doExactMatching = ....// your code decides
    TermMiner miner;
    if (exact) {
        miner = TermMiner.defaultNonFuzzyMapper(this.ontology);
    } else {
        miner = TermMiner.defaultFuzzyMapper(this.ontology);
    }

You can use fenominal to retrieve three types of objects.

sentences
^^^^^^^^^

Retrieve a collection of ``MinedSentence`` objects that represent each of the sentences in the input string in
which at least one HPO term is indentified. Each MinedSentence object has a collection of MinedTermWithMetadata objects.

.. code-block:: java

    String inputString = ....// your code provides input String
    Collection<MinedSentence> setences = miner.mineSentences(inputString);

MinedTermWithMetadata
^^^^^^^^^^^^^^^^^^^^^

Returns a collection of MinedTermWithMetadata objects, each of which provides the following methods

* ``String getMatchingString()``: the matching string in the original text
* ``double getSimilarity()`` the similaroty score for the match
* ``TermId getTermId()``: the HPO TermId
* ``int getTokenCount()``: - the number of matching tokens

Additionally, all of the methods of MinedTerm are provided (see below)


.. code-block:: java

    String inputString = ....// your code provides input String
    Collection<MinedTermWithMetadata> setences = miner.mineTermsWithMetadata(inputString);


MinedTerm
^^^^^^^^^

Returns a collection of MinedTerm objects, each of which provides the following methods

* ``int getBegin()``: zero-based start coordinate of the match in the original text
* ``int getEnd()``: zero-based end coordinate (included) of the match in the original text
* ``String getTermIdAsString()``: String version of the HPO term id.
* ``boolean isPresent()``: true of the HPO term was observed, false if it was excluded according to the original text

.. code-block:: java

    String inputString = ....// your code provides input String
    Collection<MinedTerm> sentences = miner.mineTerms(inputString);