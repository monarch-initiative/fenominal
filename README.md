# Fenominal

![Java CI with Maven](https://github.com/monarch-initiative/fenominal/workflows/Java%20CI%20with%20Maven/badge.svg)
[![Documentation Status](https://readthedocs.org/projects/fenominal/badge/?version=latest)](https://fenominal.readthedocs.io/en/latest/?badge=latest)


Phenomenal text mining for disease and phenotype concepts.

This repository comprises a programming library as well as a small command-line application for testing.
Many users will prefer the [GUI Version of fenominal](https://github.com/monarch-initiative/fenominal-gui).

Please consult the Read the docs site for [detailed documentation](https://fenominal.readthedocs.io/en/latest).

## How to build

Fenominal is written in Java 17, and thus it must be built using Java 17 and newer. 

Please refer to one of Java distributions to learn how to install the appropriate version on your platform of choice.

Assuming Java was installed and is available on path, Fenominal is built by running:

```bash
./mvnw package
```

> **Note:** if you get the error ``error: invalid target release: 16``, this probably means
that your `JAVA_HOME` is not set and Maven sees the wrong Java Development Kit (JDK). You must upgrade the JDK 
> to version 16 or higher. 


## Running CLI app

To kick the tires with an example file, assuming the build completed successfully, do the following
```bash
java -jar fenominal-cli/target/fenominal-cli-${project.version}.jar download
java -jar fenominal-cli/target/fenominal-cli-${project.version}.jar parse -i fenominal-cli/src/main/resources/noonan6vignette.txt 

# (you should see):
...
cardiomyopathy (HP:0001638;99-113)
hypertelorism (HP:0000316;327-340)
bilateral (HP:0012832;375-384)
ptosis (HP:0000508;385-391)
all (HP:0000001;533-536)
```

`cardiomyopathy (HP:0001638;99-113)` means that Cardiomyopathy (HP:0001638) 
was found at positions 99-113 of the original text (zero-based).

