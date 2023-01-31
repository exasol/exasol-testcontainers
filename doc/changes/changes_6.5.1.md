# Test Containers for Exasol on Docker 6.5.1, released 2023-01-31

Code name: Improve SSH credential handling

## Summary

Starting with this release ETC uses a global temporary directory for the SSH credentials instead of a local directory as before. The advantage is that `mvn clean` does not delete the credentials and the running container can be reused instead of starting a new one with newly created credentials. This speeds up tests especially when working with multiple projects.

This release also reduces log verbosity when starting or reusing an Exasol container.

## Features

* #220: Used global temporary directory for SSH credentials

## Bugfixes

* #219: Fixed driver manager to use correct JAR name in manifest

## Dependency Updates

### Compile Dependency Updates

* Updated `com.exasol:bucketfs-java:2.6.0` to `3.0.0`
* Removed `org.slf4j:slf4j-jdk14:2.0.6`

### Test Dependency Updates

* Added `org.slf4j:slf4j-jdk14:1.7.36`

### Plugin Dependency Updates

* Updated `org.itsallcode:openfasttrace-maven-plugin:1.5.0` to `1.6.1`
