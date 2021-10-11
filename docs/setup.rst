.. _rstsetup:

Setting up fenominal
====================

fenominal is a Java application written in Java 16. We provide precompiled versions of the GUI application
that can be run without installing a Java runtime environment on Windows, and most users should download the
installers from the [releases page of the fenominal repository](https://github.com/monarch-initiative/fenominal/releases).
The rest of this page describes how to compile and build fenomimal locally.

Prerequisites
~~~~~~~~~~~~~

fenominal was written with Java version 16. If you want to
build fenominal from source, then the build process described below requires
`Git <https://git-scm.com/book/en/v2>`_ and `maven <https://maven.apache.org/install.html>`_.

Build
~~~~~

Go the GitHub page of `fenominal <https://github.com/monarch-initiative/fenominal>`_, and clone the project.
Build the executable from source with maven, and then test the build. ::

    $ git clone https://github.com/monarch-initiative/fenominal
    $ cd fenominal
    $ ./mvnw package
    $ java -jar fenominal-cli/target/fenominal-cli-${project.version}.jar
    Usage: fenominal [-hV] [COMMAND]
    phenotype/disease NER
      -h, --help      Show this help message and exit.
      -V, --version   Print version information and exit.
    Commands:
      download, D    Download files for fenominal
      parse, P       Parse text
      supplement, S  Parse supplement with multiple affected persons



Prebuilt fenominal executable
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. TODO - update the section

This is still work in progress.


.. _rstexomiserdatadir:


The download command
~~~~~~~~~~~~~~~~~~~~

.. _rstdownload:

fenominal requires several files to run.

1. ``hp.obo``. The main Human Phenotype Ontology file

fenominal offers a convenience function to download all files to a local directory.
By default, fenominal will download all four files into a newly created subdirectory
called ``data`` in the current working directory. You can change this default with the ``-d`` or ``--data`` options
(If you change this, then you will need to pass the location of your directory to all other fenominal commands
using the ``-d`` flag). Download the files automatically as follows. ::

    $ java -jar fenominal-cli-${project.version}.jar download

fenominal will not download the files if they are already present unless the ``--overwrite`` argument is passed. For
instance, the following command would download the four files to a directory called datafiles and would
overwrite any previously downloaded files. ::

    $ java -jar fenominal-cli-${project.version}.jar download -d datafiles --overwrite


If desired, you can download these files on your own but you need to place them all in the
same directory to run fenominal.

