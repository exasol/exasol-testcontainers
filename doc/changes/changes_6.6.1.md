# Test Containers for Exasol on Docker 6.6.1, released 2023-07-06

Code name: Driver Manager Fix

## Summary

In this release we fixed a bug that prevented installation of JDBC drivers via the driver manager.

Test matrix changes:

* Removed now discontinued Exasol 7.0
* Upgraded from 7.1.20 to 7.1.21 
* Upgraded prerelease-8.17.0 to 8.18.1

## Features

* #228: Fixed bug in driver manager

## Dependency Updates

### Compile Dependency Updates

* Added `org.apache.derby:derbyclient:10.14.2.0`
* Updated `org.testcontainers:jdbc:1.18.1` to `1.18.3`
* Updated `org.testcontainers:testcontainers:1.18.1` to `1.18.3`

### Test Dependency Updates

* Added `com.exasol:udf-api-java:1.0.2`
* Added `org.apache.derby:derbynet:10.14.2.0`
* Updated `org.mockito:mockito-junit-jupiter:5.3.1` to `5.4.0`
* Updated `org.testcontainers:junit-jupiter:1.18.1` to `1.18.3`

### Plugin Dependency Updates

* Updated `org.basepom.maven:duplicate-finder-maven-plugin:1.5.1` to `2.0.1`
* Updated `org.itsallcode:openfasttrace-maven-plugin:1.6.1` to `1.6.2`
