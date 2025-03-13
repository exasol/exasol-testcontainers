# Test Containers for Exasol on Docker 7.1.4, released 2025-03-13

Code name: Fix NoSuchMethodError at startup

## Summary

This release fixes the following exception that was logged during the startup of an Exasol container:

```
java.lang.NoSuchMethodError: 'long org.apache.commons.io.file.attribute.FileTimes.toUnixTime(java.nio.file.attribute.FileTime)'
```

## Bugfixes

* #267: Fixed NoSuchMethodError at startup

## Dependency Updates

### Compile Dependency Updates

* Updated `com.github.mwiede:jsch:0.2.23` to `0.2.24`
* Updated `org.testcontainers:jdbc:1.20.4` to `1.20.6`
* Updated `org.testcontainers:testcontainers:1.20.4` to `1.20.6`

### Runtime Dependency Updates

* Updated `com.exasol:exasol-jdbc:24.2.1` to `25.2.2`
* Added `commons-io:commons-io:2.18.0`

### Test Dependency Updates

* Updated `nl.jqno.equalsverifier:equalsverifier:3.18.1` to `3.19.1`
* Added `org.junit.jupiter:junit-jupiter-api:5.12.0`
* Removed `org.junit.jupiter:junit-jupiter-engine:5.11.4`
* Updated `org.junit.jupiter:junit-jupiter-params:5.11.4` to `5.12.0`
* Updated `org.mockito:mockito-junit-jupiter:5.15.2` to `5.16.0`
* Updated `org.testcontainers:junit-jupiter:1.20.4` to `1.20.6`
