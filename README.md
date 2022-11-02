# Getting Started

## What is cardgame ?

cardgame, for now, provides two games engines : war game and "belote contrée". 

It allows me to practice multiple programming skills :

* implements more complex things that you usually code in a web business app.
* apply multithreading concepts learned by reading but rarely practiced.

War game was just a warm-up. The final objective of the project is a try to provide a contree game with a modern dynamic GUI.

## How to build 

Java 17 is required. I suggest to install the JDK with [SdkMan](https://sdkman.io/).

## How to run contree games

### You can play a contree game in text mode with 3 (stupid) bots who never bid by running this command :

```bash
java -jar target/cardgame-*-jar-with-dependencies.jar
```

### You can run a "4 bots" game by adding the "only-bots" property :
```bash
java -Donly-bots=true -jar target/cardgame-*-jar-with-dependencies.jar
```

### Default score to reach to end a game is 1000. You can change the max score with the "max-score" property : 
```bash
java -Dmax-score=500 -jar target/cardgame-*-jar-with-dependencies.jar
```

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [What is a trick-taking game](https://en.wikipedia.org/wiki/Trick-taking_game)
* [Official JUnit5 documentation](https://junit.org/junit5/docs/current/user-guide/)
* [Official Mockito documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
