# Test Containers for Exasol on Docker 7.2.0, released 2025-10-28

Code name: Update Default Exasol Version to 2025.1.3

## Summary

This release updates the default Exasol version from v7 to the latest 2015.1.3. We now run integration tests using Exasol versions 2015.1.3, 8.29.12 and 7.1.30.

**Note:** This release upgrades the [major version of the Testcontainers library](https://github.com/testcontainers/testcontainers-java/releases/tag/2.0.0) to 2.0.1.

## Features

* #278: Update default Exasol version from v7 to 2015.1.3

## Dependency Updates

### Compile Dependency Updates

* Updated `com.exasol:bucketfs-java:3.2.3` to `4.0.0`
* Updated `com.exasol:database-cleaner:1.1.4` to `1.1.5`
* Updated `com.github.mwiede:jsch:2.27.2` to `2.27.5`
* Removed `org.apache.commons:commons-compress:1.26.1`
* Removed `org.apache.commons:commons-lang3:3.18.0`
* Removed `org.testcontainers:jdbc:1.21.3`
* Added `org.testcontainers:testcontainers-jdbc:2.0.1`
* Updated `org.testcontainers:testcontainers:1.21.3` to `2.0.1`

### Runtime Dependency Updates

* Updated `com.exasol:exasol-jdbc:25.2.4` to `25.2.5`

### Test Dependency Updates

* Updated `com.exasol:hamcrest-resultset-matcher:1.7.1` to `1.7.2`
* Updated `com.exasol:udf-api-java:1.0.6` to `1.0.7`
* Removed `org.junit.jupiter:junit-jupiter-api:5.13.3`
* Updated `org.junit.jupiter:junit-jupiter-params:5.13.3` to `5.13.4`
* Updated `org.mockito:mockito-junit-jupiter:5.18.0` to `5.20.0`
* Removed `org.testcontainers:junit-jupiter:1.21.3`
* Added `org.testcontainers:testcontainers-junit-jupiter:2.0.1`

### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:2.0.3` to `2.0.5`
* Updated `com.exasol:project-keeper-maven-plugin:5.2.2` to `5.4.3`
* Updated `com.exasol:quality-summarizer-maven-plugin:0.2.0` to `0.2.1`
* Updated `io.github.git-commit-id:git-commit-id-maven-plugin:9.0.1` to `9.0.2`
* Updated `org.apache.maven.plugins:maven-artifact-plugin:3.6.0` to `3.6.1`
* Updated `org.apache.maven.plugins:maven-clean-plugin:3.4.1` to `3.5.0`
* Updated `org.apache.maven.plugins:maven-compiler-plugin:3.14.0` to `3.14.1`
* Updated `org.apache.maven.plugins:maven-enforcer-plugin:3.5.0` to `3.6.2`
* Updated `org.apache.maven.plugins:maven-failsafe-plugin:3.5.3` to `3.5.4`
* Updated `org.apache.maven.plugins:maven-gpg-plugin:3.2.7` to `3.2.8`
* Updated `org.apache.maven.plugins:maven-javadoc-plugin:3.11.2` to `3.12.0`
* Updated `org.apache.maven.plugins:maven-surefire-plugin:3.5.3` to `3.5.4`
* Updated `org.codehaus.mojo:flatten-maven-plugin:1.7.0` to `1.7.3`
* Updated `org.codehaus.mojo:versions-maven-plugin:2.18.0` to `2.19.1`
* Updated `org.jacoco:jacoco-maven-plugin:0.8.13` to `0.8.14`
* Updated `org.sonarsource.scanner.maven:sonar-maven-plugin:5.1.0.4751` to `5.2.0.4988`
* Updated `org.sonatype.central:central-publishing-maven-plugin:0.7.0` to `0.9.0`
