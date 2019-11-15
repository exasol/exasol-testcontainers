# exasol-testcontainers

<img alt="exasol-testcontainer logo" src="doc/images/exasol-testcontainer_128x128.png" style="float:left; padding:0px 10px 10px 10px;"/>

[![Build Status](https://api.travis-ci.com/exasol/exasol-testcontainers.svg?branch=master)](https://travis-ci.org/exasol/exasol-testcontainers)

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

## Scope

This software is intended for use in automated integration tests of Java software that uses Exasol. It sets up and runs a disposable Docker container and lets users access the interfaces of the Exasol instance inside that container with minimum effort.

**Don't use testcontainers for production environments or with confidential data.** To make testing as convenient as possible, the handling of the disposable containers is not up to the same security standards as a production system.

## Features

* Download, start and stop an Exasol docker container automatically
* JUnit integration via annotations
* JDBC connection to the database inside the container
* BucketFS access

## Table of Contents

### Information for Users

"Users" from the perspective of this project are software developers integrating the Exasol test container into their test environment, not database end users.

* [User Guide](doc/user_guide/user_guide.md)

### Information for Contributors

Requirement, design documents and coverage tags are written in [OpenFastTrace](https://github.com/itsallcode/openfasttrace) format.

* [System Requirement Specification](doc/system_requirements.md)
* [Design](doc/design.md)

## Dependencies

### Run Time Dependencies

Running the Exasol Testcontainers requires a Java Runtime version 11 or later.

| Dependency                                                                          | Purpose                                                | License                       |
|-------------------------------------------------------------------------------------|--------------------------------------------------------|-------------------------------|
| [Testcontainers](https://www.testcontainers.org/)                                   | Docker Container control abstraction                   | MIT License                   |

### Test Dependencies

| Dependency                                                                          | Purpose                                                | License                       |
|-------------------------------------------------------------------------------------|--------------------------------------------------------|-------------------------------|
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
| [OpenFastTrace Maven Plugin](https://github.com/itsallcode/openfasttrace-maven-plugin) | Requirement Tracing                                 | GPL v3                        |
