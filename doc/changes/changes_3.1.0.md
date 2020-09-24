# Exasol Test Containers 3.1.0, released 2020-09-XX

Code name: Exasol 7.0.0 support

## Summary

This release adds support for Exasol 7.0.0

From that version on Docker-DB changed the default ports.
If you don't set the ports explicitly (using `withExposedPorts()`) this container will auto detect the ports.
You can also access the internal ports directly using `getDefaultInternalBucketfsPort()` and `getDefaultInternalDatabasePort()`.

This version also marked the unused methods `withConnectTimeoutSeconds` and `withStartupTimeout` as such using an exception.

In addition this release changed updated the default image to 7.0.1.
 

## Features / Enhancements
 
* #78 Added Support for Exasol 7.0

## Bugfixes

* #83: Increased connection timeout

## Dependency updates

