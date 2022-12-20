# Test Containers for Exasol on Docker 6.4.1, released 2022-12-20

Code name: Dependency Upgrade on top of 6.4.0

## Summary

Updated dependencies in particular to enable use in `exasol-test-setup-abstraction-java` which depends on `bucketfs-java` directly as well as transitively via `exasol-testcontainers`.

## Changes

* #213: Updated dependencies

## Dependency Updates

### Compile Dependency Updates

* Updated `com.exasol:bucketfs-java:2.4.1` to `2.6.0`
* Updated `org.slf4j:slf4j-jdk14:2.0.5` to `2.0.6`

### Test Dependency Updates

* Updated `org.mockito:mockito-junit-jupiter:4.9.0` to `4.10.0`
