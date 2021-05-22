# fenominal
Phenomenal text mining for disease and phenotype concepts









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

