# Introduction

## Acknowledgments

This document's section structure is derived from the "[arc42](https://arc42.org/)" architectural template by Dr. Gernot Starke, Dr. Peter Hruschka.

# Constraints

This section introduces technical system constraints.

## Exasol Docker Container Requires Privileged Mode
`const~exasol-docker-container-requires-privileged-mode~1`

The [Exasol Docker container needs to run in privileged mode](https://github.com/exasol/docker-db#privileged-mode) in order to work properly.

Needs: dsn

# Solution Strategy

Exasol is [available as Docker image on DockerHub](https://hub.docker.com/r/exasol/docker-db) this is the basis for setting up a integration test environment with Exasol with reasonable effort on machines that have Docker installed.

That also works on Travis-CI which we at Exasol use as continuous integration platform for our Open Source offerings.

## Requirement Overview

Please refer to the [System Requirement Specification](system_requirements.md) for user-level requirements.

# Building Blocks

This section introduces the building blocks of the software. Together those building blocks make up the big picture of the software structure.

## `ExasolContainer`

The `ExasolContainer` is an extension of the `JdbcContainer` provided with the base `testcontainers` project responsible for managing the Exasol docker container instance.

Needs: impl

## `ClusterConfiguration`

The `ClusterConfiguration` is a set of objects representing the setup of an Exasol cluster.

Needs: impl

## `ConfigurationParser`

The `ConfigurationParser` parses Exasol's cluster configuration format.

## `Bucket`

The `Bucket` building block controls interaction with a bucket in BucketFS. 

# Runtime

This section describes the runtime behavior of the software.

## Docker-based Exasol Instance

### `testcontainers` Framework Controls Docker Image Download
`dsn~testcontainer-framework-controls-docker-image-download~1`

The `testcontainers` framework uses Docker's own facilities to download the Exasol docker image in case it is not in the local Docker cache.

Covers:

* `req~exasol-docker-image-download~1`

Needs: external

### `ExasolContainer` Controls Docker Container
`dsn~exasol-container-controls-docker-container~1`

The `ExasolContainer` controls the underlying Exasol Docker container through the `testcontainers` framework.

Covers:

* `req~docker-container-control~1`

Needs: impl, itest

### `ExasolContainer` Starts with Test
`dsn~exasol-container-starts-with-test~1`

JUnit starts the Exasol Container before the test cases in a test class.

Covers:

* `req~container-starts-with-test~1`

Needs: itest

### `ExasolContainer` Ready Criteria
`dsn~exasol-container-ready-criteria~3`

The `ExasolContainer` declares itself ready to be used after the following criteria are fulfilled:

* SQL statements can be issued via the JDBC interface
* BucketFS service is ready
* UDF language container is extracted completely (implies BucketFS ready)

Covers:

* `req~container-ready-check~1`

Needs: impl, itest

### `ExasolContainer` Uses Privileged Mode
`dsn~exasol-container-uses-privileged-mode~1`

The `ExasolContainer` tells the `testcontainers` framework to start the Docker container in privileged mode.

Covers:

* `const~exasol-docker-container-requires-privileged-mode~1`

Needs: impl, itest

## Database Access

### `ExasolContainer` Provides a JDBC Connection for Username and Password
`dsn~exasol-container-provides-a-jdbc-connection-for-username-and-password~1`

The `ExasolContainer` can create a JDBC connection for a given combination of username and password.

Covers:

* `req~jdbc-connection-for-username-and-password~1`

Needs: impl, itest

### Default JDBC Connection With SYS Credentials
`dsn~default-jdbc-connection-with-sys-credentials~1`

Unless the integrator provides database user credentials with the creation of the `ExasolContainer`, the container uses the `SYS` user and its default password.

Comment:

Note that we are talking about a disposable integration test environment with non-confidential test data here. This convenience function would not be acceptable in a production environment.

Covers:

* `req~jdbc-connection-with-administrator-privileges~1`

Needs: impl, itest

### ExaLoader in Common Docker Network
`dsn~exaloader-in-common-docker-network~1`

ETC allows running two test container in the same docker network so that the ExaLoader is able to execute `IMPORT` statements that import data from one database into the other.

Covers:

* `req~exaloader-between-two-containers~1`

Needs: impl

## BucketFS Access

### List of `Bucket`Contents
`dsn~bucket-lists-its-contents~1`

The `Bucket` lists its contents as a set of object names.

Covers:

* `req~bucket-content-listing~1`

Needs: impl, itest

### Uploading to `Bucket`
`dsn~uploading-to-bucket~1`

The `Bucket` offers uploading files from a locally accessible filesystem to a bucket in BucketFS.

Covers:

* `req~uploading-a-file-to-bucketfs~1`

Needs: impl, itest

### Uploading Strings to `Bucket`
`dsn~uploading-strings-to-bucket~1`

The `Bucket` offers uploading strings into a file in bucket in BucketFS.

Covers:

* `req~uploading-text-to-a-file-in-bucketfs~1`

Needs: impl, itest

### `BucketFactory` Injects Access Credentials
`dsn~bucket-factory-injects-access-credentials~1`

The `BucketFactory` injects the bucket access credentials from the `ClusterConfiguration` into a `Bucket` upon creating it.

Covers:

* `req~bucket-authentication~1`

Needs: impl, utest

### Waiting Until File Appears in Target Directory
`dsn~waiting-until-file-appears-in-target-directory~1`

When uploading a file into a bucket, users can choose to block the call until the file appears in the bucket's target directory.

Covers:

* `req~waiting-for-bucket-content-synchronization~1`

Needs: impl, itest

### Waiting Until Archive Extracted
`dsn~waiting-until-archive-extracted~1`

When uploading an archive of type `.tar.gz` or `.zip` into a bucket, users can choose to block the call until the archive is fully extracted in the bucket's target directory.

Covers:

* `req~waiting-for-bucket-content-synchronization~1`

Needs: impl, itest

## Log Access

### Mapping the Cluster Log Directory to the Host
`dsn~mapping-the-log-directory-to-the-host~1`

The `ExasolContainer` lets integrators map the cluster log directory to a configurable directory on the host.

Covers:

* `req~reading-log-files~1`

Needs: impl, itest

# Cross-cutting Concerns

# Design Decisions

# Quality Scenarios

# Risks
