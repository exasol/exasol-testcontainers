# Exasol Test Containers 4.0.0, released 2021-07-28

Code name: Compatibility for Development Versions of docker-db

## Summary

Release 4.0.0 brings compatibility with development versions of Exasol's [docker-db](https://github.com/exasol/docker-db). This allows writing integration tests at an earlier stage in the development cycle. For example ETC now accepts docker images with names like `exasol/docker-db:7.1-RC` or short forms containing suffixes like `7.1-beta`.

Users can now download an archive bundle that contains the server logs, configuration and core-dumps. This is especially useful in continuous integration scenarios, as it opens a convenient way of getting all relevant information for a root-cause analysis in one place.

Matrix tests now run with multiple Exasol versions on GitHub.

The release also now contributes to Exasol's central [error catalog](https://exasol.github.io/error-catalog/).

## Breaking Changes

* `bucket-fs-java` was updated to 2.0.1, which changed the method signature or some methods in order to offer better exceptions.
* Deprecated methods were removed from `ExasolDockerImageReference`.

## Features

* #142: Added support for non-development Exasol docker version numbers like `7.1-RC1`.
* #144: Added API to download server logs

## Bugfixes

* #139: Migrated from Travis CI to GitHub actions
* #146: Fixed Sonar findings. Pinned transitive dependency `org.apache.commons:commons-compress` to version 1.21 to fix CVE-2021-36090.
* #148: Fixed main branch in SonarCloud.

## Dependency Updates

### Compile Dependency Updates

* Updated `com.exasol:bucketfs-java:1.0.0` to `2.0.1`
* Updated `junit:junit:4.13.1` to `4.13.2`
* Added `org.apache.commons:commons-compress:1.21`
* Updated `org.slf4j:slf4j-jdk14:1.7.30` to `1.7.32`
* Updated `org.testcontainers:jdbc:1.15.1` to `1.16.0`
* Updated `org.testcontainers:testcontainers:1.15.1` to `1.16.0`

### Runtime Dependency Updates

* Updated `com.exasol:exasol-jdbc:7.0.4` to `7.0.11`

### Test Dependency Updates

* Updated `org.junit.jupiter:junit-jupiter-engine:5.7.1` to `5.7.2`
* Updated `org.junit.jupiter:junit-jupiter-params:5.7.1` to `5.7.2`
* Updated `org.mockito:mockito-junit-jupiter:3.7.7` to `3.11.2`
* Updated `org.testcontainers:junit-jupiter:1.15.1` to `1.16.0`

### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:0.1.1` to `0.5.0`
* Updated `com.exasol:project-keeper-maven-plugin:0.6.1` to `0.10.0`
