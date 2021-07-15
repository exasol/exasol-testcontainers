# Exasol Test Containers 3.5.2, released 2021-04-09

Code name: BucketFS and database cleaner as external dependencies

## Summary

In this release we moved the BucketFS Java API and the database cleaner to their own repositories.

## Refactoring

* #126: Moved database cleaner to its own repository
* #130: Refactored to use Bucket from bucketfs-java

## Dependency Updates

### Compile Dependency Updates

* Added `com.exasol:bucketfs-java:1.0.0`
* Added `com.exasol:database-cleaner:1.0.0`

### Plugin Dependency Updates

* Added `com.exasol:error-code-crawler-maven-plugin:0.1.1`
* Updated `com.exasol:project-keeper-maven-plugin:0.4.2` to `0.6.1`
* Added `io.github.zlika:reproducible-build-maven-plugin:0.13`
