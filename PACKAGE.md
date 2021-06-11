# Package Fenominal as a self-contained Java application 

The packaging must be done on the target platform. E.g. to create the native app for Linux, the packaging must be 
done on Linux, using the Linux JDK.

---

## Packaging

First, let's setup several variables that we use across all target platforms. 

```bash
NAME="Fenominal"
VERSION=0.0.3
JAR_NAME=fenominal-gui-${VERSION}.jar
VENDOR="The Monarch Initiative"
DESCRIPTION="Phenomenal text mining for disease and phenotype concepts"
COPYRIGHT="Copyright 2021, All rights reserved"
ICON="fenominal-gui/target/classes/rose" # Update icon once we have a better one
```

Having the variables in place, let's build the native apps! 

### Linux package (DEB)

To build a DEB installer, run the following command on a Linux machine:

```bash
cd fenominal
./mvnw clean package
jpackage --input fenominal-gui/target --main-jar ${JAR_NAME} --name ${NAME} --app-version ${VERSION} --description "${DESCRIPTION}" --vendor "${VENDOR}" --license-file LICENSE --copyright "${COPYRIGHT}" --linux-menu-group "Science" --linux-shortcut --icon "${ICON}.png" 
```

The command builds a `fenominal_0.0.3-1_amd64.deb` package file in the current directory. To (un)install the package, run:

```bash
sudo dpkg -i fenominal_0.0.3-1_amd64.deb
sudo dpkg --purge fenominal
```

The app is installed into `/opt` folder. A shortcut *Fenominal* is available to launch the app from the launcher/dock.

### Windows installer (EXE)

Build the installer for Windows.

```bash
dir fenominal
mvnw.cmd clean package 
jpackage --input fenominal-gui/target --main-jar ${JAR_NAME} --name ${NAME} --app-version ${VERSION} --description "${DESCRIPTION}" --vendor "${VENDOR}" --license-file LICENSE --copyright "${COPYRIGHT}" --win-per-user-install
```

The installer allows installs the app into user-specific folder (`C:\Users\user-name\AppData\Local\Fenominal` by default).

**TODO** - test the installation process.

### Mac OS (DMG)

Build the DMG application for macOS.

```bash
cd fenominal
./mvnw clean package
jpackage --input fenominal-gui/target --main-jar ${JAR_NAME} --name ${NAME} --app-version ${VERSION} --description "${DESCRIPTION}" --vendor "${VENDOR}" --license-file LICENSE --copyright "${COPYRIGHT}" --mac-package-name "${NAME}" --icon "${ICON}.icns"
```

After the run, a DMG file is available in the current working directory. Install the DMG by mounting and/or dragging 
the app into the *Applications* folder.

To uninstall the app, delete the *Fenominal* entry from the *Applications* folder. 

**TODO**:
- how to sign the package? See [here](https://docs.oracle.com/en/java/javase/16/jpackage/support-application-features.html#GUID-8D9F0607-91F4-4070-8823-02FCAB12238D)
- we must prepare a `1.0.0` version or better allow creating an app for macOS

## See more

See more info regarding various `jpackage` options [here](https://docs.oracle.com/en/java/javase/16/jpackage/index.html).
