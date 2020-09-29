# Exasol Test Containers 3.1.0, released 2020-09-29

Code name: Exasol 7.0.0 support

## Summary

This release adds support for Exasol 7.0.0

In this version of the Docker-DB, the default ports have changed.
If you don't set the ports explicitly (using `withExposedPorts()`) the container will auto detect the ports.
We have added two new methods to access internal ports directly using `getDefaultInternalBucketfsPort()` and `getDefaultInternalDatabasePort()`.

This version also marked the unused methods `withConnectTimeoutSeconds` and `withStartupTimeout`. Calling these methods throws an `UnsupportedOperation` exception.

In addition this release changed the default image version to 7.0.2.
 

## Features / Enhancements
 
* #78: Added Support for Exasol 7.0.0
* #79: Added GitHub actions

## Bugfixes

* #83: Increased connection timeout

## Dependency updates
