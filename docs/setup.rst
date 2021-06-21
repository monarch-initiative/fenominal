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


Installation of HpoTextMiner as a Java library
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
fenominal relies on the [HpoTextMining](https://github.com/monarch-initiative/HpoTextMining) library. Currently,
fenomimal requires the java16 branch of HpoTextMining. This is how to install in on your machine. ::

    $ https://github.com/monarch-initiative/HpoTextMining.git
    $ cd HpoTextMining

Now, we ensure that we are using the correct branch of HpoTextMining (release-12.0.0). ::

    $ git checkout java16

Finally, we use the maven system to install the HpoTextMining library locally so that it can be used by fenominal. ::

    $ mvn install

This command will install the library in the ``.m2`` directory located in your home directory.

Build
~~~~~

Go the GitHub page of `fenominal <https://github.com/monarch-initiative/fenominal>`_, and clone the project.
Build the executable from source with maven, and then test the build. ::

    $ git clone https://github.com/monarch-initiative/fenominal
    $ cd fenominal
    $ mvn package
    $ java -jar target/LIRICAL.jar
    $ Usage: <main class> [options] [command] [command options]
      Options:
        -h, --help
          display this help message
      (...)



LIRICAL requires `maven <https://maven.apache.org/>`_ version 3.5.3.


Prebuilt LIRICAL executable
^^^^^^^^^^^^^^^^^^^^^^^^^^^

Alternatively, go to the `Releases section <https://github.com/TheJacksonLaboratory/LIRICAL/releases>`_ on the
LIRICAL GitHub page and download the latest precompiled version of LIRICAL.



.. _rstexomiserdatadir:


Exomiser database files
~~~~~~~~~~~~~~~~~~~~~~~


LIRICAL uses data files from the Exomiser. We recommend that always the latest version of these files be used. The
data files are stored at the `Exomiser download site <https://monarch-exomiser-web-dev.monarchinitiative.org/exomiser/download>`_.
You may need to scroll (right hand side) to see the subdirectory ``latest``, which includes the current version of
these files. Download either ``1909_hg19.zip`` (for the hg19/GRCh37 genome assembly)  or ``1909_hg38.zip `` for the
hg38/GRCh38 assembly). Of course, the datafile you use should match the assembly used to align and call
the exome/genome data you want to analyze with LIRICAL.  Unpack the file, e.g., ::

    $ unzip 1909_hg19.zip

Remember the path, since it will be needed to run LIRICAL with exome/genome data. We will use the argument: ::

    -e /some/path/1909_hg19

where ``1909_hg19`` is the directory that is created by unpacking the archive file. The directory should contain 10
files including:

* 1909_hg19_genome.h2.db
* 1909_hg19_transcripts_ensembl.ser
* 1909_hg19_transcripts_refseq.ser
* 1909_hg19_transcripts_ucsc.ser
* 1909_hg19_variants.mv.db

These files are used by LIRICAL to annotate the VCF file and support variant interpretation.





The download command
~~~~~~~~~~~~~~~~~~~~

.. _rstdownload:

LIRICAL requires four additional files to run.

1. ``hp.obo``. The main Human Phenotype Ontology file
2. ``phenotype.hpoa`` The main annotation file with all HPO disease models
3. ``Homo_sapiens_gene_info.gz`` A file from NCBI Entrez Gene with information about human genes
4. ``mim2gene_medgen`` A file from the NCBI medgen project with OMIM-derived links between genes and diseases

LIRICAL offers a convenience function to download all four files
to a local directory. By default, LIRICAL will download all four files into a newly created subdirectory
called ``data`` in the current working directory. You can change this default with the ``-d`` or ``--data`` options
(If you change this, then you will need to pass the location of your directory to all other LIRICAL commands
using the ``-d`` flag). Download the files automatically as follows. ::

    $ java -jar LIRICAL.jar download

LIRICAL will not download the files if they are already present unless the ``--overwrite`` argument is passed. For
instance, the following command would download the four files to a directory called datafiles and would
overwrite any previously downloaded files. ::

    $ java -jar LIRICAL.jar download -d datafiles --overwrite


If desired, you can download these files on your own but you need to place them all in the
same directory to run LIRICAL.

