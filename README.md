# fenominal
Phenomenal text mining for disease and phenotype concepts






# building: tltr

We are using version of https://github.com/monarch-initiative/HpoCaseAnnotator that is on the java16 branch.
You need to install this locally with mvn install. 

```bazaar
export JAVA_HOME=$(readlink -f /usr/bin/javac | sed "s:/bin/javac::")
```

Then build this app for instance with the maven wrapper.




# set up
The release version of the library should be compatible with Java 11 and newer. We will release the GUI as a standalone
app and are building it with Java 16. To install Java 16 on an ubuntu system, follow these steps

```bazaar
sudo add-apt-repository ppa:linuxuprising/java
sudo apt update
sudo apt install oracle-java16-installer # ( --no-install-recommends would not install Java16 as the default)
# if desired -- sudo apt-get remove oracle-java16-installer (de-install)
(notes)
update-alternatives: using /usr/lib/jvm/java-16-oracle/bin/jpackage to provide /
usr/bin/jpackage (jpackage) in auto mode
Oracle JDK 16 installed
#####Important########
To set Oracle JDK 16 as default, install the "oracle-java16-set-default" package
E.g.: sudo apt install oracle-java16-set-default.
```

Note that if you get the error ``error: invalid target release: 16``, this probably means
that your JAVA_HOME is not set and maven sees the wrong JDK. This is the problem
```bazaar
$ java -version
java version "16.0.1" 2021-04-20
Java(TM) SE Runtime Environment (build 16.0.1+9-24)
Java HotSpot(TM) 64-Bit Server VM (build 16.0.1+9-24, mixed mode, sharing)
$ mvn -version
Apache Maven 3.6.3
Maven home: /usr/share/maven
Java version: 11.0.11, vendor: Ubuntu, runtime: /usr/lib/jvm/java-11-openjdk-amd64
Default locale: en_US, platform encoding: UTF-8
OS name: "linux", version: "5.4.0-73-generic", arch: "amd64", family: "unix"
```
Here, we need to find the location of the Java 16 installation, e.g., ``/usr/lib/jvm/java-16-oracle/``
and then add the following to .bashrc
```bazaar
export JAVA_HOME=/usr/lib/jvm/java-16-oracle/
```


# Running CLI app

To kick the tires with an example file, do the following
```bazaar
mvn package
java -jar fenominal-cli/target/fenominal.jar download
java -jar fenominal-cli/target/fenominal.jar parse -i fenominal-cli/src/main/resources/noonan6vignette.txt 
(you should see)
...
cardiomyopathy (HP:0001638;99-113)
hypertelorism (HP:0000316;327-340)
bilateral (HP:0012832;375-384)
ptosis (HP:0000508;385-391)
all (HP:0000001;533-536)
```

cardiomyopathy (HP:0001638;99-113) means that cardiomyopathy (HP:0001638) 
was found at positions 99-113 of the original text (zero-based).






### Parking the module
module fenominal.core {
// TODO - it would be nice to think more about the API of this module to prevent having to export all packages
exports org.monarchinitiative.fenominal.core;
exports org.monarchinitiative.fenominal.core.corenlp;
exports org.monarchinitiative.fenominal.core.textmapper;
exports org.monarchinitiative.fenominal.core.except;
exports org.monarchinitiative.fenominal.json;

    requires phenol.core;
    //requires static stanford.corenlp;
    requires static org.slf4j;
    requires static com.google.common;
    requires static curie.util;
    requires static org.yaml.snakeyaml;
    //requires static com.fasterxml.jackson.annotation;
    //requires static com.fasterxml.jackson.databind;
    //requires com.fasterxml.jackson.databind; // TODO - remove as soon we get rid of the IO module here

}



/// cli
module-info.java

module fenominal.cli {
requires fenominal.core;

// requires org.monarchinitiative.phenol.phenol.core;
requires info.picocli;
requires org.apache.commons.net;

    requires org.slf4j;
}

/// gui

module fenominal.gui {
requires fenominal.core;
requires hpotextmining.gui;

    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    requires spring.context;
    requires spring.boot.autoconfigure;
    requires spring.beans;
    requires spring.core;
    requires spring.boot;

    requires lucene.sandbox;
}