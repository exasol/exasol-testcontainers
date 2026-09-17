# Test Containers for Exasol on Docker 8.1.0, released 2026-09-17

Code name: Reverse Port Forwarding

## Summary

This release adds reverse SSH port forwarding that makes local TCP services available from Exasol UDFs.

## Features

* #297: Added reverse SSH port forwarding that makes local TCP services available from Exasol UDFs.

## Dependency Updates

### Compile Dependency Updates

* Updated `com.exasol:bucketfs-java:5.0.0` to `5.0.1`
* Updated `com.exasol:database-cleaner:2.0.0` to `2.0.1`

### Test Dependency Updates

* Updated `com.exasol:hamcrest-resultset-matcher:1.7.3` to `1.7.4`
* Updated `com.exasol:udf-api-java:1.0.11` to `1.0.12`

### Plugin Dependency Updates

* Updated `com.exasol:project-keeper-maven-plugin:5.7.5` to `5.7.6`
* Updated `org.apache.maven.plugins:maven-compiler-plugin:3.15.0` to `3.16.0`
* Updated `org.apache.maven.plugins:maven-failsafe-plugin:3.5.6` to `3.6.0`
* Updated `org.apache.maven.plugins:maven-surefire-plugin:3.5.6` to `3.6.0`
* Updated `org.itsallcode:openfasttrace-maven-plugin:2.3.1` to `3.0.0`
* Updated `org.sonarsource.scanner.maven:sonar-maven-plugin:5.7.0.6970` to `5.8.0.7211`
