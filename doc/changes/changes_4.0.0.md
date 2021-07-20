# Exasol Test Containers 4.0.0, released 2021-07-16

Code name: Compatibility for Development Versions of docker-db

## Summary

Release 4.0.0 brings compatibility with development versions of Exasol's [docker-db](https://github.com/exasol/docker-db). This allows writing integration tests at an earlier stage in the development cycle.

Matrix tests now run with multiple Exasol versions on GitHub.

## Breaking Changes

* `bucket-fs-java` was updated to 2.0.1, which changed the method signature or some methods in order to offer better exceptions.
* Deprecated methods were removed from `ExasolDockerImageReference`.

## Features

* #142: ETC now accepts non-development version numbers like `7.1-RC1`.

## Bugfixes

* #139: Migrated from Travis CI to GitHub actions

## Dependency Updates

### Compile Dependency Updates

* Updated `com.exasol:bucketfs-java:1.0.0` to `2.0.1`
* Updated `junit:junit:4.13.1` to `4.13.2`
* Updated `org.slf4j:slf4j-jdk14:1.7.30` to `1.7.31`
* Updated `org.testcontainers:jdbc:1.15.1` to `1.15.3`
* Updated `org.testcontainers:testcontainers:1.15.1` to `1.15.3`

### Runtime Dependency Updates

* Updated `com.exasol:exasol-jdbc:7.0.4` to `7.0.7`

### Test Dependency Updates

* Updated `org.junit.jupiter:junit-jupiter-engine:5.7.1` to `5.7.2`
* Updated `org.junit.jupiter:junit-jupiter-params:5.7.1` to `5.7.2`
* Updated `org.mockito:mockito-junit-jupiter:3.7.7` to `3.11.2`
* Updated `org.testcontainers:junit-jupiter:1.15.1` to `1.15.3`

### Plugin Dependency Updates

* Updated `com.exasol:project-keeper-maven-plugin:0.6.1` to `0.10.0`
