.. _rstmatching:

===============================
Fenominal's Matching Algorithms
===============================

Fenominal performs both exact and fuzzy (T-BLAT) matching. In both cases, the order of the tokens of each HPO term
can be present in any order and stop words are ignored. For instance,
`Mallet finger HP:0030771 <https://hpo.jax.org/app/browse/term/HP:0030771>`_ will be inferred from both ``mallet finger``
and ``finger mallet``. For the term `Anomalous hepatic venous drainage into the left atrium HP:0032181 <https://hpo.jax.org/app/browse/term/HP:0032181>`_,
the stopwords ``the`` and ``into`` are not considered.


Exact matching
==============

The exact matching algorithm searches for exact matches to term or synonym labels, ignoriing stop words, whereby the
tokens can be present in any order in the text.


T-BLAT (fuzzy) matching
=======================

TODO -- short description




