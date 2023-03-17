fenominal: Phenomenal text mining for disease and phenotype concepts
====================================================================


fenominal
~~~~~~~~~

Fenominal is a Java 17 library for text-mining
`Human Phenotype Ontology (HPO) <http://www.human-phenotype-ontology.org>`_ terms from text. Fenominal is
a multimodule project with a ``core`` module with the text-mining logic and a ``cli`` module for use from the
command line. A graphical user interface (GUI) is available in a separate project
called `Fenominal-GUI <https://github.com/monarch-initiative/fenominal-gui>`_.

Fenominal implements the T-BLAT algorithm, which is inspired by the BLAST algorithm for biosequence alignment.
T-BLAT screens texts for potential matches on the basis of matching k-mer counts and scores candidates based on
conformance to typical patterns of spelling errors derived from 2.9 million clinical notes. Fenominal also implements
exact matching but matches also on multitoken HPO labels or synonyms that are permuted.


Fenominal does not rely on external APIs and can be used in settings in which a firewall does not permit applications
to access the internet.  Fenominal is intended for use as a software library and the CLI module only
contains simple demo applications.


.. toctree::
   :maxdepth: 1
   :caption: Contents:

   matching
   cli-app
   usage

