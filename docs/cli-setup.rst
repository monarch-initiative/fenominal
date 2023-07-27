.. _rstclisetup:

========================
CLI fenominal app: Setup
========================

This page describes how to build and use the
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
    $ java -jar fenominal-cli/target/fenominal-cli-0.7.10.jar
        Usage: fenominal [-hV] [COMMAND]
        phenotype/disease NER
            -h, --help      Show this help message and exit.
            -V, --version   Print version information and exit.
        Commands:
            download, D  Download files for fenominal
            parse, P     Parse text


