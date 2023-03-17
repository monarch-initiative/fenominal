.. _rstsetup:

=========================
CLI fenominal application
=========================

fenominal is a Java library written in Java 17. fenominal contains a command-line interface (cli) module
that demonstrates some of the functionality of the library.

See :ref:`rstclisetup` for instructions on building the application.

See :ref:`rstclidownload` for instructions on how to download the ``hp.json`` file that is needed for the app.

Once you have built the application, you should see this

.. code-block:: bash

    java -jar fenominal-cli/target/fenominal.jar
        Usage: fenominal [-hV] [COMMAND]
        phenotype/disease NER
            -h, --help      Show this help message and exit.
            -V, --version   Print version information and exit.
        Commands:
            download, D  Download files for fenominal
            parse, P     Parse text

in the following we will show only ``fenominal.jar``. Adjust the path accordingly.

Fenominal's Matching
====================

Fenominal can perform exact matching and fuzzy matching. See :ref:`rstmatching` for an explanation.

To see the options, run ``java -jar fenominal.jar parse -h``.


.. _tbl-grid:

+----------------+------------------+----------------------------------------------+
|   Short option | Long option      | Explanation                                  |
+================+==================+==============================================+
| -e             |  --exact         | Use exact matching algorithm                 |
+----------------+------------------+----------------------------------------------+
| -h             |  --help          | Show this help message and exit.             |
+----------------+------------------+----------------------------------------------+
|                |  --hp=<path>     | Path to HP json file (default data/hp.json)  |
+----------------+------------------+----------------------------------------------+
|  -i            | --input=<path>   | Path to HP json file (default data/hp.json)  |
+----------------+------------------+----------------------------------------------+
|  -0            | --output=<path>  | Path to output file                          |
+----------------+------------------+----------------------------------------------+
|  -V            | --version        | Print version information and exit.          |
+----------------+------------------+----------------------------------------------+
|                | --verbose        | Show parse results in shell                  |
+----------------+------------------+----------------------------------------------+





Exact matching
^^^^^^^^^^^^^^

For this example, create a file called ``text-exact.txt`` with the following contents


    A 28-year-old woman who was diagnosed with Noonan syndrome at age 4 because of growth retardation, cardiomyopathy, and hypertelorism.


Run fenominal as follows.

.. code-block:: bash

   java -jar fenominal.jar parse --exact -i text-exact.txt --verbose
   (...)
   Growth delay	HP:0001510	growth retardation	observed	79	97	A 28-year-old woman who was diagnosed with Noonan syndrome at age 4 because of growth retardation, cardiomyopathy, and hypertelorism.
   Cardiomyopathy	HP:0001638	cardiomyopathy	observed	99	113	A 28-year-old woman who was diagnosed with Noonan syndrome at age 4 because of growth retardation, cardiomyopathy, and hypertelorism.
   Hypertelorism	HP:0000316	hypertelorism	observed	119	132	A 28-year-old woman who was diagnosed with Noonan syndrome at age 4 because of growth retardation, cardiomyopathy, and hypertelorism.


T-BLAT (fuzzy) Matching
^^^^^^^^^^^^^^^^^^^^^^^

For this example, create a file called ``text-errors.txt`` with the following contents.

    A 28-year-old woman who was diagnosed with Noonan syndrome at age 4 because of growth retadation, cardiomyopathic, and hypertelorisn.

Run fenominal as follows.

.. code-block:: bash

   java -jar fenominal.jar parse -i text-errors.txt --verbose
   (...)
    Agnosia	HP:0010524	agnosia	observed	28	37	A 28-year-old woman who was diagnosed with Noonan syndrome at age 4 because of growth retadation, cardiomyopathic, and hypertelorisn.
    Growth delay	HP:0001510	growth retardation	observed	79	96	A 28-year-old woman who was diagnosed with Noonan syndrome at age 4 because of growth retadation, cardiomyopathic, and hypertelorisn.
    Cardiomyopathy	HP:0001638	cardiomyopathy	observed	98	113	A 28-year-old woman who was diagnosed with Noonan syndrome at age 4 because of growth retadation, cardiomyopathic, and hypertelorisn.
    Hypertelorism	HP:0000316	hypertelorism	observed	119	132	A 28-year-old woman who was diagnosed with Noonan syndrome at age 4 because of growth retadation, cardiomyopathic, and hypertelorisn.

Fenominal picks up four terms. Agnosia is false postion (from the word diagnosed). The remaining three terms are
inferred correctly despite the presence of spelling errors or variants. Note that T-BLAT is the default
approach, and exact matching is only performed if the ``--exact`` flag is passed.
