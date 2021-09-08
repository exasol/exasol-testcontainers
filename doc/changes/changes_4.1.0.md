# Test containers for Exasol on Docker 4.1.0, released 2021-09-08

Code name: JSON-RPC Support

## Summary

Release 4.1.0 now supports using the RPC interface of the Exasol database container.

It automatically forwards the relevant port and allows retrieving the mapped port. You can also retrieve the token required for Bearer token authentication of the RPC interface.

RPC allows you to manage buckets and more. You can use the version 2.2.0 and later of [bucketfs-java](https://github.com/exasol/bucketfs-java) to do this.

## Features

* [#161](https://github.com/exasol/exasol-testcontainers/issues/161): Support using RPC interface

## Dependency Updates

### Runtime Dependency Updates

* Updated `com.exasol:exasol-jdbc:7.0.11` to `7.1.0`

### Test Dependency Updates

* Updated `org.mockito:mockito-junit-jupiter:3.11.2` to `3.12.4`

### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:0.5.0` to `0.6.0`
* Updated `com.exasol:project-keeper-maven-plugin:0.10.0` to `1.0.0`
* Updated `org.apache.maven.plugins:maven-enforcer-plugin:3.0.0-M3` to `3.0.0`
* Updated `org.apache.maven.plugins:maven-gpg-plugin:1.6` to `3.0.1`
* Updated `org.apache.maven.plugins:maven-javadoc-plugin:3.2.0` to `3.3.1`
* Updated `org.itsallcode:openfasttrace-maven-plugin:1.0.0` to `1.2.0`
* Updated `org.jacoco:jacoco-maven-plugin:0.8.6` to `0.8.7`
