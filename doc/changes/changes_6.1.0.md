# Test containers for Exasol on Docker 6.1.0, released 2022-02-18

Code name: Getting TLS certificate fingerprints, cross-container import for 7.1+

## Summary

This release adds a method `ExasolContainer.getTlsCertificateFingerprint()` that returns the TLS certificate fingerprint of the Exasol database that is required to verify the certificate during tests.

Cross-container `IMPORT` now also works with version 7.1 when using the TLS fingerprint in the `EXA` connection.

## Features

* #182: Added method for retrieving the TLS certificate fingerprint

## Bugfixes

* #156: Fixed test case for cross-container import with Exasol 7.1
* #180: Moved fingerprint to JDBC parameter for driver backward compatibility

## Dependency Updates

### Compile Dependency Updates

* Updated `org.slf4j:slf4j-jdk14:1.7.32` to `1.7.36`
* Updated `org.testcontainers:jdbc:1.16.2` to `1.16.3`
* Updated `org.testcontainers:testcontainers:1.16.2` to `1.16.3`

### Runtime Dependency Updates

* Updated `com.exasol:exasol-jdbc:7.1.2` to `7.1.4`

### Test Dependency Updates

* Updated `org.junit.jupiter:junit-jupiter-engine:5.8.1` to `5.8.2`
* Updated `org.junit.jupiter:junit-jupiter-params:5.8.1` to `5.8.2`
* Updated `org.mockito:mockito-junit-jupiter:4.1.0` to `4.3.1`
* Updated `org.testcontainers:junit-jupiter:1.16.2` to `1.16.3`

### Plugin Dependency Updates

* Updated `io.github.zlika:reproducible-build-maven-plugin:0.14` to `0.15`
* Updated `org.apache.maven.plugins:maven-compiler-plugin:3.8.1` to `3.10.0`
* Updated `org.apache.maven.plugins:maven-javadoc-plugin:3.3.1` to `3.3.2`
* Updated `org.codehaus.mojo:versions-maven-plugin:2.8.1` to `2.9.0`
* Updated `org.itsallcode:openfasttrace-maven-plugin:1.2.0` to `1.4.0`
* Updated `org.sonatype.ossindex.maven:ossindex-maven-plugin:3.1.0` to `3.2.0`
* Updated `org.sonatype.plugins:nexus-staging-maven-plugin:1.6.8` to `1.6.10`
