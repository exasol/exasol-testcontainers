# Test Containers for Exasol on Docker 7.1.6, released 2025-06-05

Code name: Upgrade dependencies on top of 7.1.5

## Summary

This release updates compile and plugin dependencies of the project to fix transitive security issues.

# Security

* #269: Fixed CVE-2024-55551 in `com.exasol:exasol-jdbc:jar:25.2.2:runtime`

## Dependency Updates

### Compile Dependency Updates

* Updated `com.exasol:bucketfs-java:3.2.1` to `3.2.3`

### Plugin Dependency Updates

* Updated `com.exasol:project-keeper-maven-plugin:5.0.1` to `5.1.0`
* Updated `org.apache.maven.plugins:maven-failsafe-plugin:3.5.2` to `3.5.3`
* Updated `org.apache.maven.plugins:maven-surefire-plugin:3.5.2` to `3.5.3`
* Updated `org.jacoco:jacoco-maven-plugin:0.8.12` to `0.8.13`
* Updated `org.sonarsource.scanner.maven:sonar-maven-plugin:5.0.0.4389` to `5.1.0.4751`
