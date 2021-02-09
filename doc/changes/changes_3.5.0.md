# Exasol Test Containers 3.5.0, released 2021-02-09

Code name: Host IP detector

## Summary

In the 3.5.0 release of the Exasol Test Containers, we added a new function `getHostIp()` that allows getting the IP
address of the host the Exasol docker container runs on. This is useful if you want to connect to a debugger or profiler.

Note that this feature is at the moment only available on Linux.

We also updated the default `docker-db` image version to 7.0.6.

## Features

* #58: Support `getHostIp()` on Linux

## Bug fixes

* 122: Updated base Test Containers versions to 1.15.1 to prevent build failures

## Dependency updates
 
### Test Dependency Updates

* Updated `org.junit.jupiter:junit-jupiter-engine:5.7.0` to `5.7.1`
* Updated `org.junit.jupiter:junit-jupiter-params:5.7.0` to `5.7.1`
* Updated `org.mockito:mockito-junit-jupiter:3.6.28` to `3.7.7`
* Updated `org.testcontainers:jdbc:1.15.0` to `1.15.1`
* Updated `org.testcontainers:junit-jupiter:1.15.0` to `1.15.1`
* Updated `org.testcontainers:testcontainers:1.15.0` to `1.15.1`