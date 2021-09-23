# Test containers for Exasol on Docker 5.1.0, released 2021-09-24

Code name: Support TLS Certificates

## Summary

Version 5.1.0 of the Exasol Testcontainers adds support for TLS encrypted connections. When the container has started, all DB connections will be automatically encrypted and verify the fingerprint of the container's TLS certificate with option `validateservercertificate=1`.

## Features

* (#159)[https://github.com/exasol/exasol-testcontainers/issues/159]: Certificate handling for Exasol 7.1

## Bugfixes

* Fixed timeout waiting for BucketFS to be available when reusing containers

## Dependency Updates

### Runtime Dependency Updates

* Updated `com.exasol:exasol-jdbc:7.1.0` to `7.1.1`

### Plugin Dependency Updates

* Updated `com.exasol:project-keeper-maven-plugin:1.1.0` to `1.2.0`
