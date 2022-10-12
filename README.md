# Getting Started

## What is cardgame ?

cardgame, for now, provides two games engines : war game and "belote contrée". 

It allows me to practice multiple programming skills :

* implements more complex things that you usually code in a web business app.
* apply multithreading concepts learned by reading but rarely practiced.

War game was just a warm up. The final objective of the project is a try to provide a contree game with a modern dynamic GUI.

## How to build 

Java 17 is required. I suggest to install the JDK with [SdkMan](https://sdkman.io/).

Once JAVA 17 installed, just run : 

    ./mvnw package
    java -jar ./target/cardgame-*.jar

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.7.1/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/2.7.1/maven-plugin/reference/html/#build-image)
* [JOOQ Access Layer](https://docs.spring.io/spring-boot/docs/2.7.1/reference/htmlsingle/#data.sql.jooq)
* [Liquibase Migration](https://docs.spring.io/spring-boot/docs/2.7.1/reference/htmlsingle/#howto.data-initialization.migration-tool.liquibase)

