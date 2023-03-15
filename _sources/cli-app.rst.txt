.. _rstsetup:

CLI fenominal application
=========================

fenominal is a Java library written in Java 17. fenominal contains a command-line interface (cli) module
that demonstrates some of the functionality of the library. This page describes how to build and use the
cli application.

Prerequisites
~~~~~~~~~~~~~

fenominal was written with Java version 17. If you want to
build fenominal from source, then the build process described below requires
`Git <https://git-scm.com/book/en/v2>`_ and `maven <https://maven.apache.org/install.html>`_.

Build
~~~~~

Go the GitHub page of `fenominal <https://github.com/monarch-initiative/fenominal>`_, and clone the project.
Build the executable from source with maven, and then test the build. ::

    $ git clone https://github.com/monarch-initiative/fenominal
    $ cd fenominal
    $ ./mvnw package
    $ java -jar fenominal-cli/target/fenominal-cli-0.7.3.jar
        Usage: fenominal [-hV] [COMMAND]
        phenotype/disease NER
            -h, --help      Show this help message and exit.
            -V, --version   Print version information and exit.
        Commands:
            download, D  Download files for fenominal
            kmer, K      Create kmer DB files
            parse, P     Parse text


The download command
~~~~~~~~~~~~~~~~~~~~

.. _rstdownload:

fenominal requires several files to run.

1. ``hp.json``. The main Human Phenotype Ontology file

fenominal offers a convenience function to download all files to a local directory.
By default, fenominal will download all four files into a newly created subdirectory
called ``data`` in the current working directory. You can change this default with the ``-d`` or ``--data`` options
(If you change this, then you will need to pass the location of your directory to all other fenominal commands
using the ``-d`` flag). Download the files automatically as follows. ::

    $ java -jar fenominal-cli-0.7.3.jar download

fenominal will not download the files if they are already present unless the ``--overwrite`` argument is passed. For
instance, the following command would download the four files to a directory called datafiles and would
overwrite any previously downloaded files. ::

    $ java -jar fenominal-cli-0.7.3.jar download -d datafiles --overwrite


