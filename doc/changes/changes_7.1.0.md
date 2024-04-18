# Test Containers for Exasol on Docker 7.1.0, released 2024-04-18

Code name: Install Custom SLC

## Summary

This release allows installing custom Script Language Containers (SLC) into the Exasol Docker container. This is useful for testing UDFs with a SLC containing additional libraries or a newer Java version. See the [user guide](https://github.com/exasol/exasol-testcontainers/blob/main/doc/user_guide/user_guide.md#installing-custom-script-language-containers-slc) for detailed usage instructions.

### Refactoring

This release updates the following methods to throw the runtime exception `UncheckedSqlException` instead of the checked exception `SqlException`. This simplifies exception handling in clients.

* `ExasolContainer.createConnectionForUser()`
* `ExasolContainer.createConnection()`

## Features

* #250: Added support for installing custom Script Language Containers

## Bugfixes

* #252: Replaced unmaintained SSH library `com.jcraft:jsch` with `com.github.mwiede:jsch`

## Dependency Updates

### Compile Dependency Updates

* Updated `com.exasol:bucketfs-java:3.1.1` to `3.1.2`
* Updated `com.exasol:database-cleaner:1.1.2` to `1.1.3`
* Added `com.github.mwiede:jsch:0.2.17`
* Removed `com.jcraft:jsch:0.1.55`
* Updated `org.testcontainers:jdbc:1.19.5` to `1.19.7`
* Updated `org.testcontainers:testcontainers:1.19.5` to `1.19.7`

### Runtime Dependency Updates

* Updated `com.exasol:exasol-jdbc:7.1.20` to `24.1.0`
* Updated `org.apache.commons:commons-compress:1.26.0` to `1.26.1`

### Test Dependency Updates

* Added `com.exasol:hamcrest-resultset-matcher:1.6.5`
* Updated `com.exasol:udf-api-java:1.0.4` to `1.0.5`
* Added `com.jparams:to-string-verifier:1.4.8`
* Added `nl.jqno.equalsverifier:equalsverifier:3.16.1`
* Updated `org.mockito:mockito-junit-jupiter:5.10.0` to `5.11.0`
* Updated `org.testcontainers:junit-jupiter:1.19.5` to `1.19.7`

### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:1.3.1` to `2.0.2`
* Updated `com.exasol:project-keeper-maven-plugin:3.0.1` to `4.3.0`
* Updated `org.apache.maven.plugins:maven-compiler-plugin:3.11.0` to `3.13.0`
* Updated `org.apache.maven.plugins:maven-failsafe-plugin:3.2.3` to `3.2.5`
* Updated `org.apache.maven.plugins:maven-gpg-plugin:3.1.0` to `3.2.2`
* Updated `org.apache.maven.plugins:maven-surefire-plugin:3.2.3` to `3.2.5`
* Updated `org.codehaus.mojo:flatten-maven-plugin:1.5.0` to `1.6.0`
* Updated `org.jacoco:jacoco-maven-plugin:0.8.11` to `0.8.12`
* Updated `org.sonarsource.scanner.maven:sonar-maven-plugin:3.10.0.2594` to `3.11.0.3922`
