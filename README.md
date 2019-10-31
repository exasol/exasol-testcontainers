# exasol-testcontainers

[![Build Status](https://api.travis-ci.org/exasol/exasol-testcontainers.svg?branch=master)](https://travis-ci.org/exasol/exasol-testcontainers)

SonarCloud results:

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Aexasol-testcontainers&metric=alert_status)](https://sonarcloud.io/dashboard?id=com.exasol%3Aexasol-testcontainers)

[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Aexasol-testcontainers&metric=security_rating)](https://sonarcloud.io/dashboard?id=com.exasol%3Aexasol-testcontainers)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Aexasol-testcontainers&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=com.exasol%3Aexasol-testcontainers)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Aexasol-testcontainers&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=com.exasol%3Aexasol-testcontainers)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Aexasol-testcontainers&metric=sqale_index)](https://sonarcloud.io/dashboard?id=com.exasol%3Aexasol-testcontainers)

[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Aexasol-testcontainers&metric=code_smells)](https://sonarcloud.io/dashboard?id=com.exasol%3Aexasol-testcontainers)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Aexasol-testcontainers&metric=coverage)](https://sonarcloud.io/dashboard?id=com.exasol%3Aexasol-testcontainers)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Aexasol-testcontainers&metric=duplicated_lines_density)](https://sonarcloud.io/dashboard?id=com.exasol%3Aexasol-testcontainers)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Aexasol-testcontainers&metric=ncloc)](https://sonarcloud.io/dashboard?id=com.exasol%3Aexasol-testcontainers)

This project provides an abstraction to Exasol running on Docker for the purpose of integration testing based on [Java Testcontainers](https://www.testcontainers.org).

You can create a dockerized instance of Exasol with a few Java commands from within the test framework (e.g. [JUnit](https://junit.org)).

## Dependencies

### Run Time Dependencies

Running the Virtual Schema requires a Java Runtime version 9 or later.

| Dependency                                                                          | Purpose                                                | License                       |
|-------------------------------------------------------------------------------------|--------------------------------------------------------|-------------------------------|
| [Testcontainers](https://www.testcontainers.org/)                                   | Docker Container control abstraction                   | MIT License                   |

### Test Dependencies

| Dependency                                                                          | Purpose                                                | License                       |
|-------------------------------------------------------------------------------------|--------------------------------------------------------|-------------------------------|
| [Equals Verifier](https://jqno.nl/equalsverifier/)                                  | Testing `equals(...)` and `hashCode()` contracts       | Apache License 2.0            |
| [Java Hamcrest](http://hamcrest.org/JavaHamcrest/)                                  | Checking for conditions in code via matchers           | BSD License                   |
| [JUnit](https://junit.org/junit5)                                                   | Unit testing framework                                 | Eclipse Public License 1.0    |
| [Mockito](http://site.mockito.org/)                                                 | Mocking framework                                      | MIT License                   |

### Build Dependencies

| Plug-in                                                                             | Purpose                                                | License                       |
|-------------------------------------------------------------------------------------|--------------------------------------------------------|-------------------------------|
| [Apache Maven](https://maven.apache.org/)                                           | Build tool                                             | Apache License 2.0            |
| [Maven Compiler Plugin](https://maven.apache.org/plugins/maven-compiler-plugin/)    | Setting required Java version                          | Apache License 2.0            |
| [Maven GPG Plugin](https://maven.apache.org/plugins/maven-gpg-plugin/)              | Code signing                                           | Apache License 2.0            |
| [Maven Javadoc Plugin](https://maven.apache.org/plugins/maven-javadoc-plugin/)      | Creating a Javadoc JAR                                 | Apache License 2.0            |
| [Maven Jacoco Plugin](https://www.eclemma.org/jacoco/trunk/doc/maven.html)          | Code coverage metering                                 | Eclipse Public License 2.0    |
| [Maven Source Plugin](https://maven.apache.org/plugins/maven-source-plugin/)        | Creating a source code JAR                             | Apache License 2.0            |
| [Maven Surefire Plugin](https://maven.apache.org/surefire/maven-surefire-plugin/)   | Unit testing                                           | Apache License 2.0            |