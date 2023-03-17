.. _rstclidownload:

===========================================
CLI fenominal app: Downloading the hpo file
===========================================

The fenominal command-line app requires a copy of ``hp.json``, the main Human Phenotype Ontology file. The
file can be manually downloaded from the `HPO Website <https://hpo.jax.org/app/>`_.

Alternatively, fenominal offers a convenience function to download all files to a local directory.
By default, fenominal will download all four files into a newly created subdirectory
called ``data`` in the current working directory. You can change this default with the ``-d`` or ``--data`` options
(If you change this, then you will need to pass the location of your directory to all other fenominal commands
using the ``-d`` flag). Download the files automatically as follows. ::

    $ java -jar fenominal.jar download

fenominal will not download the files if they are already present unless the ``--overwrite`` argument is passed. For
instance, the following command would download the four files to a directory called datafiles and would
overwrite any previously downloaded files. ::

    $ java -jar fenominal.jar download -d datafiles --overwrite

If you download the hp.json file manually or use any non-default name for the directory in which the file is located,
you need to indicate the path of the directory using the -d argument for the parse command.
