# Test Containers for Exasol on Docker 7.0.0, released 2023-11-22

Code name: Removed `withClusterLogsPath()`

## Summary

This release removes method `ExasolContainer.withClusterLogsPath()` because the underlying method `addFileSystemBind()` is broken. We decided to remove the method because it is not widely used.

**Note:** This release excludes vulnerability CVE-2022-46337 in `org.apache.derby:derby:jar:10.14.2.0` which is required only for tests. Newer versions don't support Java 8 any more.

## Breaking Change

* #240: Removed `ExasolContainer.withClusterLogsPath()`

## Dependency Updates

### Compile Dependency Updates

* Updated `com.exasol:database-cleaner:1.1.1` to `1.1.2`
* Removed `org.apache.commons:commons-compress:1.24.0`
* Removed `org.apache.derby:derbyclient:10.14.2.0`
* Updated `org.testcontainers:jdbc:1.19.1` to `1.19.2`
* Updated `org.testcontainers:testcontainers:1.19.1` to `1.19.2`

### Test Dependency Updates

* Updated `com.exasol:udf-api-java:1.0.3` to `1.0.4`
* Added `org.apache.derby:derbyclient:10.14.2.0`
* Updated `org.junit-pioneer:junit-pioneer:2.1.0` to `2.2.0`
* Updated `org.testcontainers:junit-jupiter:1.19.1` to `1.19.2`

### Plugin Dependency Updates

* Updated `com.exasol:project-keeper-maven-plugin:2.9.15` to `2.9.16`
* Updated `org.apache.maven.plugins:maven-javadoc-plugin:3.6.0` to `3.6.2`
* Updated `org.apache.maven.plugins:maven-surefire-plugin:3.1.2` to `3.2.2`
