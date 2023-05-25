# Test Containers for Exasol on Docker 6.6.0, released 2023-05-25

Code name: Support for Exasol 8 Pre-Releases

## Summary  

To be prepared for the upcoming version 8 of Exasol, this version of the Exasol Test Containers adds support for pre-releases using a prefix in the name of Docker Containers.

Added `prerelease-8.17.0` to the CI test matrix. Default version is now 7.1.20.

## Known Limitations

Due to changed TLS behavior in 8 pre-release, the Exasol Testcontainer does not work with TLS on version Exasol version 8 yet. Support will be added with #232.

## Features

* #200: Test with Exasol database version 8 pre-release

## Dependency Updates

### Compile Dependency Updates

* Updated `org.testcontainers:jdbc:1.18.0` to `1.18.1`
* Updated `org.testcontainers:testcontainers:1.18.0` to `1.18.1`

### Runtime Dependency Updates

* Updated `com.exasol:exasol-jdbc:7.1.19` to `7.1.20`

### Test Dependency Updates

* Updated `org.junit-pioneer:junit-pioneer:2.0.0` to `2.0.1`
* Updated `org.junit.jupiter:junit-jupiter-engine:5.9.2` to `5.9.3`
* Updated `org.junit.jupiter:junit-jupiter-params:5.9.2` to `5.9.3`
* Updated `org.mockito:mockito-junit-jupiter:5.2.0` to `5.3.1`
* Updated `org.testcontainers:junit-jupiter:1.18.0` to `1.18.1`

### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:1.2.2` to `1.2.3`
* Updated `com.exasol:project-keeper-maven-plugin:2.9.6` to `2.9.7`
* Updated `org.apache.maven.plugins:maven-compiler-plugin:3.10.1` to `3.11.0`
* Updated `org.apache.maven.plugins:maven-deploy-plugin:3.1.0` to `3.1.1`
* Updated `org.apache.maven.plugins:maven-enforcer-plugin:3.2.1` to `3.3.0`
* Updated `org.apache.maven.plugins:maven-failsafe-plugin:3.0.0-M8` to `3.0.0`
* Updated `org.apache.maven.plugins:maven-javadoc-plugin:3.4.1` to `3.5.0`
* Updated `org.apache.maven.plugins:maven-surefire-plugin:3.0.0-M8` to `3.0.0`
* Updated `org.codehaus.mojo:flatten-maven-plugin:1.3.0` to `1.4.1`
* Updated `org.codehaus.mojo:versions-maven-plugin:2.14.2` to `2.15.0`
* Updated `org.jacoco:jacoco-maven-plugin:0.8.8` to `0.8.9`
