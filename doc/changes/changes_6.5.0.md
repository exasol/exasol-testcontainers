# Test Containers for Exasol on Docker 6.5.0, released 2023-01-17

Code name: Improved Support for SSH Credentials

## Summary

Improved support for SSH credentials. See [User Guide](../user_guide/user_guide.md#ssh-access-and-temporary-credentials) for details.

## Features

* #215: Improved auto-detection of folder for temporary SSH credentials
* #217: Updated dependencies

## Dependency Updates

### Compile Dependency Updates

* Updated `com.exasol:database-cleaner:1.0.2` to `1.1.0`

### Runtime Dependency Updates

* Updated `com.exasol:exasol-jdbc:7.1.16` to `7.1.17`

### Test Dependency Updates

* Updated `org.junit.jupiter:junit-jupiter-engine:5.9.1` to `5.9.2`
* Updated `org.junit.jupiter:junit-jupiter-params:5.9.1` to `5.9.2`
* Updated `org.mockito:mockito-junit-jupiter:4.10.0` to `5.0.0`
