# Test Containers for Exasol on Docker 7.3.0, released 2026-05-08

Code name: Nano support

## Summary

This release adds basic support for the [Exasol Nano container](https://hub.docker.com/r/exasol/nano). See the [user guide](../user_guide/user_guide.md#using-exasol-nano) for usage instructions.

## Features

* #287: Add support for Exasol Nano

## Dependency Updates

### Compile Dependency Updates

* Updated `com.github.mwiede:jsch:2.27.9` to `2.28.2`
* Updated `org.testcontainers:testcontainers-jdbc:2.0.4` to `2.0.5`
* Updated `org.testcontainers:testcontainers:2.0.4` to `2.0.5`

### Test Dependency Updates

* Updated `org.testcontainers:testcontainers-junit-jupiter:2.0.4` to `2.0.5`

### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:2.0.6` to `2.0.7`
* Updated `com.exasol:project-keeper-maven-plugin:5.4.6` to `5.6.2`
* Updated `io.github.git-commit-id:git-commit-id-maven-plugin:9.0.2` to `10.0.0`
* Updated `org.apache.maven.plugins:maven-failsafe-plugin:3.5.4` to `3.5.5`
* Updated `org.apache.maven.plugins:maven-resources-plugin:3.4.0` to `3.5.0`
* Updated `org.apache.maven.plugins:maven-surefire-plugin:3.5.4` to `3.5.5`
