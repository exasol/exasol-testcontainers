# Test containers for Exasol on Docker 5.1.2, released 2021-11-26

Code name: 5.1.2 -- Dependency updates and test speed improvements

In version 5.1.2 of the Exasol Testcontainers we sped up the tests and updated the dependencies to the general test containers.

## Features

* #174: Upgraded `testcontainers` to 1.16.2 to fix security issue.

## Refactoring

* #165: Sped up test for wait strategies.

## Dependency Updates

### Compile Dependency Updates

* Removed `junit:junit:4.13.2`
* Updated `org.testcontainers:jdbc:1.16.0` to `1.16.2`
* Updated `org.testcontainers:testcontainers:1.16.0` to `1.16.2`

### Runtime Dependency Updates

* Updated `com.exasol:exasol-jdbc:7.1.1` to `7.1.2`

### Test Dependency Updates

* Updated `org.junit.jupiter:junit-jupiter-engine:5.7.2` to `5.8.1`
* Updated `org.junit.jupiter:junit-jupiter-params:5.7.2` to `5.8.1`
* Updated `org.mockito:mockito-junit-jupiter:4.0.0` to `4.1.0`
* Updated `org.testcontainers:junit-jupiter:1.16.0` to `1.16.2`

### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:0.6.0` to `0.7.1`
* Updated `com.exasol:project-keeper-maven-plugin:1.2.0` to `1.3.4`
* Updated `io.github.zlika:reproducible-build-maven-plugin:0.13` to `0.14`
