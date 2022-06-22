# Test containers for Exasol on Docker 6.1.2, released 2022-06-22

Code name: Uniform Fingerprint URL

JDBC URLs now uniformly end in a semicolon, no matter if with or without fingerprint.

We replaced all uses of TestContainers now deprecated `getContainerID` method with `getHost`.

## Bugfixes

* #193: Unified `getJdbcUrlWithFingerprint()` and `getJdbcUrlWithoutFingerprint()`

## Dependency Updates

### Compile Dependency Updates

* Updated `org.testcontainers:jdbc:1.16.3` to `1.17.2`
* Updated `org.testcontainers:testcontainers:1.16.3` to `1.17.2`

### Runtime Dependency Updates

* Updated `com.exasol:exasol-jdbc:7.1.4` to `7.1.11`

### Test Dependency Updates

* Updated `org.mockito:mockito-junit-jupiter:4.3.1` to `4.6.1`
* Updated `org.testcontainers:junit-jupiter:1.16.3` to `1.17.2`

### Plugin Dependency Updates

* Updated `org.itsallcode:openfasttrace-maven-plugin:1.4.0` to `1.5.0`
