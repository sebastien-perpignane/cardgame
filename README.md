# Getting Started

## What is cardgame ?

cardgame, for now, provides two games engines : war game and "belote contrée". 

It allows me to practice multiple programming skills :

* implements more complex things that you usually code in a web business app.
* apply multithreading concepts learned by reading but rarely practiced.

War game was just a warm-up. The final objective of the project is a try to provide a contree game with a modern dynamic GUI.

## How to build a playable jar

Java 21 is required. I suggest to install the JDK with [SdkMan](https://sdkman.io/).

```bash
./mvnw -Pstandalone clean package
```

Activating the "standalone" profile is needed if you want to play the game in console mode.

If you want to use cardgame as a jar lib, do not use "standalone" profile.

## How to run contree games

### You can play a contree game in text mode with 3 (stupid) bots who never bid by running this command :

```bash
java -jar target/cardgame-*-standalone.jar
```

### You can run a "4 bots" game by activating the "--only-bots" option :
```bash
java -jar target/cardgame-*-standalone.jar --only-bots=true
```

### Default score to reach to end a game is 1500. You can change the max score with the "--max-score" option : 
```bash
java -jar target/cardgame-*-standalone.jar --max-score=500
```

## How to "install" contree game with a native image
It is possible to generate a native image of cardgame. The pom.xml file contains all needed configuration.
You need to install GraalVM. I suggest installing a 'Liberica NIK' java 21 graalvm using sdkman. Example:
```bash
sdk install java 23.1.4.r21-nik # latest version while updating the README
```

Then, build the project with this command:
```bash
./mvnw -Pstandalone,native package
```

You'll find the native image at target/cardgame

## How to "install" contree game using jpackage

You can "install" contree-game on your linux system by running these commands (with the hypothesis that your HOME contains a
bin directory that's in your PATH) :

```bash
rm -f $HOME/bin/contree-game
./mvnw -Pstandalone package
./scripts/jpackage.sh
ln -s $(pwd)/target/jpackage/contree-game/bin/contree-game $HOME/bin/contree-game
```

### Reference Documentation
For further reference, please consider the following sections:

#### Technical doc

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Official JUnit5 documentation](https://junit.org/junit5/docs/current/user-guide/)
* [Official Mockito documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)

#### Card game doc

* [Belote contrée rules (french)](http://www.ffbelote.org/belote-contree/#6)
* [What is a trick-taking game](https://en.wikipedia.org/wiki/Trick-taking_game)
