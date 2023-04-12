# Test Containers for Exasol on Docker 6.5.2, released 2023-04-12

Code name: Remove duplicate classes from dependencies

## Summary

This release removes duplicate classes from dependencies.

## Bugfixes

* #224: Removed duplicate classes from dependencies

## Dependency Updates

### Compile Dependency Updates

* Updated `com.exasol:bucketfs-java:3.0.0` to `3.1.0`
* Updated `org.testcontainers:jdbc:1.17.6` to `1.18.0`
* Updated `org.testcontainers:testcontainers:1.17.6` to `1.18.0`

### Runtime Dependency Updates

* Updated `com.exasol:exasol-jdbc:7.1.17` to `7.1.19`

### Test Dependency Updates

* Updated `org.junit-pioneer:junit-pioneer:1.9.1` to `2.0.0`
* Updated `org.mockito:mockito-junit-jupiter:5.0.0` to `5.2.0`
* Updated `org.testcontainers:junit-jupiter:1.17.6` to `1.18.0`

### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:1.2.1` to `1.2.2`
* Updated `com.exasol:project-keeper-maven-plugin:2.9.1` to `2.9.6`
* Updated `org.apache.maven.plugins:maven-deploy-plugin:3.0.0` to `3.1.0`
* Updated `org.apache.maven.plugins:maven-enforcer-plugin:3.1.0` to `3.2.1`
* Updated `org.apache.maven.plugins:maven-failsafe-plugin:3.0.0-M7` to `3.0.0-M8`
* Updated `org.apache.maven.plugins:maven-surefire-plugin:3.0.0-M7` to `3.0.0-M8`
* Added `org.basepom.maven:duplicate-finder-maven-plugin:1.5.1`
* Updated `org.codehaus.mojo:versions-maven-plugin:2.13.0` to `2.14.2`
