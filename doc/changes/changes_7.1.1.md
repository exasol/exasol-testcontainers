# Test Containers for Exasol on Docker 7.1.1, released 2024-08-05

Code name: Fix startup of Docker DB 8.29.1

## Summary

This release fixes a `NullPointerException` when starting an Exasol Docker DB 8.29.1 or later. Starting with 8.29.1 Docker DB requires TLS encrypted connections for accessing BucketFS. Exasol Testcontainers automatically choses the right protocol depending on the DB version.

This release also updates the default Docker DB version to 7.1.29.

## Bugfix

* #258: Fixed `NullPointerException` during startup of 8.29.1

## Dependency Updates

### Compile Dependency Updates

* Updated `com.exasol:bucketfs-java:3.1.2` to `3.2.0`
* Updated `com.github.mwiede:jsch:0.2.17` to `0.2.18`
* Updated `org.testcontainers:jdbc:1.19.7` to `1.20.1`
* Updated `org.testcontainers:testcontainers:1.19.7` to `1.20.1`

### Runtime Dependency Updates

* Updated `com.exasol:exasol-jdbc:24.1.0` to `24.1.1`
* Removed `commons-codec:commons-codec:1.16.1`
* Updated `org.apache.commons:commons-compress:1.26.1` to `1.26.2`

### Test Dependency Updates

* Updated `org.hamcrest:hamcrest:2.2` to `3.0`
* Updated `org.junit.jupiter:junit-jupiter-engine:5.10.2` to `5.10.3`
* Updated `org.junit.jupiter:junit-jupiter-params:5.10.2` to `5.10.3`
* Updated `org.mockito:mockito-junit-jupiter:5.11.0` to `5.12.0`
* Updated `org.testcontainers:junit-jupiter:1.19.7` to `1.20.1`

### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:2.0.2` to `2.0.3`
* Updated `com.exasol:project-keeper-maven-plugin:4.3.0` to `4.3.3`
* Updated `org.apache.maven.plugins:maven-deploy-plugin:3.1.1` to `3.1.2`
* Updated `org.apache.maven.plugins:maven-enforcer-plugin:3.4.1` to `3.5.0`
* Updated `org.apache.maven.plugins:maven-gpg-plugin:3.2.2` to `3.2.4`
* Updated `org.apache.maven.plugins:maven-javadoc-plugin:3.6.3` to `3.7.0`
* Updated `org.apache.maven.plugins:maven-toolchains-plugin:3.1.0` to `3.2.0`
* Updated `org.sonarsource.scanner.maven:sonar-maven-plugin:3.11.0.3922` to `4.0.0.4121`
* Updated `org.sonatype.plugins:nexus-staging-maven-plugin:1.6.13` to `1.7.0`
