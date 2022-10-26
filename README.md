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
You can run contree games played by 4 stupid bots with this command  :

```bash
java -jar target/cardgame-*-jar-with-dependencies.jar
```

* It will start 1000 (more or less) simultaneous games.
* The main class used is :
  * a first test of dependency injection using CDI with Weld
  * the beginning of a resource consumption test

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
