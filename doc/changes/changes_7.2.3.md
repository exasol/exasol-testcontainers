# Test Containers for Exasol on Docker 7.2.3, released 2026-03-31

Code name: Support development version numbers

## Summary

This release adds support for development version numbers like `2026.1.0-dev.0`. Both the Docker image reference parser 
and the database version checker now handle version strings with dot-separated suffixes.

## Features

* #285: Added support for parsing development version numbers

## Dependency Updates

### Compile Dependency Updates

* Updated `com.exasol:database-cleaner:1.1.5` to `1.1.6`
* Updated `com.github.mwiede:jsch:2.27.5` to `2.27.9`
* Updated `org.testcontainers:testcontainers-jdbc:2.0.2` to `2.0.4`
* Updated `org.testcontainers:testcontainers:2.0.2` to `2.0.4`

### Runtime Dependency Updates

* Updated `com.exasol:exasol-jdbc:25.2.5` to `26.2.7`

### Test Dependency Updates

* Updated `com.exasol:udf-api-java:1.0.7` to `1.0.8`
* Updated `org.mockito:mockito-junit-jupiter:5.20.0` to `5.23.0`
* Updated `org.testcontainers:testcontainers-junit-jupiter:2.0.2` to `2.0.4`

### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:2.0.5` to `2.0.6`
* Updated `com.exasol:project-keeper-maven-plugin:5.4.3` to `5.4.6`
* Updated `org.apache.maven.plugins:maven-compiler-plugin:3.14.1` to `3.15.0`
* Updated `org.apache.maven.plugins:maven-resources-plugin:3.3.1` to `3.4.0`
* Updated `org.apache.maven.plugins:maven-source-plugin:3.2.1` to `3.4.0`
* Updated `org.codehaus.mojo:versions-maven-plugin:2.19.1` to `2.21.0`
* Updated `org.sonarsource.scanner.maven:sonar-maven-plugin:5.2.0.4988` to `5.5.0.6356`
* Updated `org.sonatype.central:central-publishing-maven-plugin:0.9.0` to `0.10.0`
