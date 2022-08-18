# Test containers for Exasol on Docker 6.2.0, released 2022-08-18

Code name: Exasol 8.0 support

## Summary

This release adds support for Exasol 8.0:
* small changes in format of entries in BucketFS log
* assuming time zone UTC for entries in BucketFS log
* disabled tests for Exasol 8 for discontinued features, e.g. ExaOperation

Additionally updated bucketfs-java to version 2.4.0 with generic state-based monitoring for SyncAwareBuckets.  State-based monitors support different strategies for filtering events from the BucketFS logfile. Besides evaluating the timestamps of the log entries as in the past another strategy is to evaluate their line number and to accept only log entries with higher line number than before triggering the monitored operation.

## Features

* #195: Upgraded to Exasol 8.0.

## Dependency Updates

### Compile Dependency Updates

* Updated `com.exasol:bucketfs-java:2.3.0` to `2.4.0`
* Removed `org.apache.commons:commons-compress:1.21`
* Updated `org.testcontainers:jdbc:1.17.2` to `1.17.3`
* Updated `org.testcontainers:testcontainers:1.17.2` to `1.17.3`

### Test Dependency Updates

* Updated `org.junit.jupiter:junit-jupiter-engine:5.8.2` to `5.9.0`
* Updated `org.junit.jupiter:junit-jupiter-params:5.8.2` to `5.9.0`
* Updated `org.mockito:mockito-junit-jupiter:4.6.1` to `4.7.0`
* Updated `org.testcontainers:junit-jupiter:1.17.2` to `1.17.3`

### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:0.7.1` to `1.1.2`
* Updated `com.exasol:project-keeper-maven-plugin:1.3.4` to `2.6.2`
* Updated `org.apache.maven.plugins:maven-compiler-plugin:3.10.0` to `3.10.1`
* Updated `org.apache.maven.plugins:maven-enforcer-plugin:3.0.0` to `3.1.0`
* Updated `org.apache.maven.plugins:maven-failsafe-plugin:3.0.0-M3` to `3.0.0-M5`
* Updated `org.apache.maven.plugins:maven-javadoc-plugin:3.3.2` to `3.4.0`
* Updated `org.apache.maven.plugins:maven-surefire-plugin:3.0.0-M3` to `3.0.0-M5`
* Added `org.codehaus.mojo:flatten-maven-plugin:1.2.7`
* Updated `org.codehaus.mojo:versions-maven-plugin:2.9.0` to `2.10.0`
* Updated `org.jacoco:jacoco-maven-plugin:0.8.7` to `0.8.8`
* Added `org.sonarsource.scanner.maven:sonar-maven-plugin:3.9.1.2184`
* Updated `org.sonatype.plugins:nexus-staging-maven-plugin:1.6.8` to `1.6.13`
