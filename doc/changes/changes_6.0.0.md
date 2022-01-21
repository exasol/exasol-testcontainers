# Test containers for Exasol on Docker 6.0.0, released 2022-01-24

Code name: Remove Exasol 6.2 (and below) support

## Refactoring

* #178 : Remove support for Exasol Database version 6.2 and lower. Throws an exception when starting Exasol 6.2 and below.

## Dependency Updates

### Compile Dependency Updates

* Updated `com.exasol:bucketfs-java:2.2.0` to `2.3.0`
* Removed `junit:junit:4.13.2`
* Updated `org.testcontainers:jdbc:1.16.0` to `1.16.2`
* Updated `org.testcontainers:testcontainers:1.16.0` to `1.16.2`

### Runtime Dependency Updates

* Updated `com.exasol:exasol-jdbc:7.1.1` to `7.1.2`

### Test Dependency Updates

* Updated `org.junit.jupiter:junit-jupiter-engine:5.7.2` to `5.8.1`
* Updated `org.junit.jupiter:junit-jupiter-params:5.7.2` to `5.8.1`
* Updated `org.mockito:mockito-junit-jupiter:4.0.0` to `4.1.0`
* Updated `org.testcontainers:junit-jupiter:1.16.0` to `1.16.2`

### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:0.6.0` to `0.7.1`
* Updated `com.exasol:project-keeper-maven-plugin:1.2.0` to `1.3.4`
* Updated `io.github.zlika:reproducible-build-maven-plugin:0.13` to `0.14`
