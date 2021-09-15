# Test containers for Exasol on Docker 5.0.0, released 2021-09-15

Code name: Improve UDF container status detection

## Summary

Version 5.0.0 of the Exasol Testcontainers improves test stability when running multiple tests while reusing containers (`.withReuse(true)`). Before, the availiability check for the UDF service (`.withRequiredServices(ExasolService.UDF)`) could cause the startup to fail with a timeout because the log pattern detector ignored log entries with an outdated timestamp:

```
org.testcontainers.containers.ContainerLaunchException: Container startup failed
...
Caused by: org.testcontainers.containers.ContainerLaunchException: Timeout: Scanning for log message pattern "ScriptLanguages.*extracted$ in "/exa/logs/cored/bucketfsd.*.log".
```

Version 5.0.0 also supports using the RPC interface of the Exasol database container.

It automatically forwards the RPC port. You can also retrieve the token required for bearer token authentication of the RPC interface using method `ExasolContainer.getClusterConfiguration().getAuthenticationToken()` and you can get the mapped RPC URL by calling method `ExasolContainer.getRpcUrl()`.

RPC allows you to manage buckets and more. You can use [bucketfs-java](https://github.com/exasol/bucketfs-java) version 2.2.0 or later to do this.

## Breaking changes

* The API and constructor of class `LogPatternDetector` has changed. It now delegates checking of the log entries to a separate class. If you created new instances using `LogPatternDetectorFactory` you won't have to change anything.

## Features

* [#161](https://github.com/exasol/exasol-testcontainers/issues/161): Added support for using RPC interface
* Changed to use docker image `exasol/docker-db:7.1.1` instead of `7.0.10` by default
* Added method `getActualLog()` to class `LogPatternDetector`. This is useful for reading the actual log file content when debugging timeout issues.

## Bugfixes

* [#163](https://github.com/exasol/exasol-testcontainers/issues/163): Fixed timeout during startup when reusing containers.

## Dependency Updates

### Runtime Dependency Updates

* Updated `com.exasol:exasol-jdbc:7.0.11` to `7.1.0`

### Test Dependency Updates

* Updated `org.mockito:mockito-junit-jupiter:3.11.2` to `3.12.4`

### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:0.5.0` to `0.6.0`
* Updated `com.exasol:project-keeper-maven-plugin:0.10.0` to `1.1.0`
* Updated `org.apache.maven.plugins:maven-enforcer-plugin:3.0.0-M3` to `3.0.0`
* Updated `org.apache.maven.plugins:maven-gpg-plugin:1.6` to `3.0.1`
* Updated `org.apache.maven.plugins:maven-javadoc-plugin:3.2.0` to `3.3.1`
* Updated `org.itsallcode:openfasttrace-maven-plugin:1.0.0` to `1.2.0`
* Updated `org.jacoco:jacoco-maven-plugin:0.8.6` to `0.8.7`
