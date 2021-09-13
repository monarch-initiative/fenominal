# Fenominal

![Java CI with Maven](https://github.com/monarch-initiative/fenominal/workflows/Java%20CI%20with%20Maven/badge.svg)
[![Documentation Status](https://readthedocs.org/projects/fenominal/badge/?version=latest)](https://fenominal.readthedocs.io/en/latest/?badge=latest)


Phenomenal text mining for disease and phenotype concepts

## How to build

Fenominal is written in Java 16, and thus it must be built using Java 16 and newer. We will release the GUI as a standalone
app, and we are building it with Java 16.

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

## Running GUI app

Assuming the build completed successfully, the GUI is ran by the following command: 

```bash
java -jar fenominal-gui/target/fenominal-gui-${project.version}.jar
```

Please refer to the documentation to learn more about various GUI features.
