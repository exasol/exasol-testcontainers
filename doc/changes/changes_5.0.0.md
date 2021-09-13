# Test containers for Exasol on Docker 5.0.0, released 2021-09-??

Code name: Improve UDF container status detection

Version 5.0.0 of the Exasol Testcontainers improves test stability when running multiple tests while reusing containers (`withReuse(true)`). Before, the availiability check for the UDF service (`.withRequiredServices(ExasolService.UDF)`) could cause the startup to fail with a timeout because the log pattern detector ignored log entries with an outdated timestamp:

```
org.testcontainers.containers.ContainerLaunchException: Container startup failed
...
Caused by: org.testcontainers.containers.ContainerLaunchException: Timeout: Scanning for log message pattern "ScriptLanguages.*extracted$ in "/exa/logs/cored/bucketfsd.*.log".
```

## Breaking changes

* The API and constructor of class `LogPatternDetector` has changed. It now delegates checking of the log entries to a separate class. If you created new instances using `LogPatternDetectorFactory` you won't have to change anything.

## Features

## Bugfixes

* [#163](https://github.com/exasol/exasol-testcontainers/issues/163): Startup fails with a timeout when reusing containers.

## Dependency Updates
