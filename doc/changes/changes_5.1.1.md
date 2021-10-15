# Test containers for Exasol on Docker 5.1.1, released 2021-10-15

Code name: Reduced log verbosity and fixed connection timeout

## Summary

In version 5.1.1 of the `exasol-testcontainers` we reduced log verbosity when reading the certificate.

We also fixed the connection test timeout which was defined in seconds but treated as milliseconds.

## Features

* [#160](https://github.com/exasol/exasol-testcontainers/issues/160): Define timeout in seconds instead of milliseconds
* [#170](https://github.com/exasol/exasol-testcontainers/issues/170): Reduced log verbosity when reading the certificate

## Dependency Updates

### Compile Dependency Updates

* Updated `com.exasol:bucketfs-java:2.1.0` to `2.2.0`

### Test Dependency Updates

* Updated `org.mockito:mockito-junit-jupiter:3.12.4` to `4.0.0`