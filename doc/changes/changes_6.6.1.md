# Test Containers for Exasol on Docker 6.6.1, released 2023-06-??

Code name: Driver Manager Fix

## Summary

In this release we fixed a bug that prevented installation of drivers via the driver manager.

## Features

* #228: Fixed bug in driver manager

## Dependency Updates

### Compile Dependency Updates

* Added `org.apache.derby:derbyclient:10.14.2.0`

### Test Dependency Updates

* Added `com.exasol:udf-api-java:1.0.2`
* Added `org.apache.derby:derbynet:10.14.2.0`
