# Introduction

## Acknowledgments

This document's section structure is derived from the "[arc42](https://arc42.org/)" architectural template by Dr. Gernot Starke, Dr. Peter Hruschka.

## Terms and Abbreviations

<dl>
    <dt>ETC</dt><dd>Exasol Test Containers</dd>
</dl>

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

## `DatabaseService`

The `DatabaseService` controls features of a service providing the database like stopping and restarting.

## `EXAoperationEmulator`

The `EXAoperationEmulator` provides an emulation of selected functions of EXAoperation &mdash; which is not included in the underlying `docker-db`.

## `Plugin`

The `Plugin` allows controlling functions of an EXAoperation plug-in.

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
* UDF language container is extracted completely

Covers:

* `req~container-ready-check~1`

Needs: impl, itest

### Defining Required Optional Services
`dsn~defining-required-optional-service~1`

The `ExasolContainer` offers an option that controls which services the user requires. The ETC waits for those services to be ready.

Covers:

* `req~defining-required-optional-services~1`

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

### IP Address in Common Docker Network
`dsn~ip-address-in-common-docker-network~1`

ETC reports the docker-network internal IP address.

Rationale:

This is the main requirement for any Exasol service to be able to reach another service in the same Docker network. Hostname resolution via Docker network alias is not supported by [`exasol/docker-db`](https://github.com/exasol/docker-db) yet.

Covers:

* `req~exaloader-between-two-containers~1`

Needs: impl, itest

### `DatabaseService` stops the Database
`dsn~database-service-stops-the-database~1`

The `DatabaseService` allows users to stop the database provided by that service.

Covers:

* `req~starting-and-stopping-the-database~1`

Needs: impl, itest

### `DatabaseService` starts the Database
`dsn~database-service-starts-the-database~1`

The `DatabaseService` allows users to start the database provided by that service.

Covers:

* `req~starting-and-stopping-the-database~1`

Needs: impl, itest

## BucketFS Access

### List of `Bucket`Contents
`dsn~bucket-lists-its-contents~1`

The `Bucket` lists its contents as a set of object names.

Covers:

* `req~bucket-content-listing~1`

Needs: impl, itest

### Uploading to `Bucket`
`dsn~uploading-to-bucket~1`

The `Bucket` offers uploading a file from a locally accessible filesystem to a bucket in BucketFS.

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

### Downloading a file from a `Bucket`
`dsn~downloading-a-file-from-a-bucket~1`

The `Bucket` offers downloading a file from a bucket in BucketFS to a locally accessible filesystem.

Covers:

* `req~downloading-a-file-from-bucketfs~1`

Needs: impl, itest

## Log Access

### Mapping the Cluster Log Directory to the Host
`dsn~mapping-the-log-directory-to-the-host~1`

The `ExasolContainer` lets integrators map the cluster log directory to a configurable directory on the host.

Covers:

* `req~reading-log-files~1`

Needs: impl, itest

### ExaOperation Emulation

EXAoperation features required for integration tests are emulated.

Interface compatibility for plug-ins means that the emulation uses the established plug-in interfaces (read "control scripts inside the plug-in").

EXAoperation's own interfaces are _not_ available. Especially there is no emulation of the XML-RPC interface. What is available though is a control object implementation of a Java interface called "EXAoperation". It is this interface that test authors can use in test code to trigger the emulated features.

#### Extracting Plug-in Packages
`dsn~extracting-plug-in-packages~1`

The `ExaOperationEmulator` unpacks plug-in packages from TAR archives with the suffix `.pgk` inside the Exasol instance.

Covers:

* `req~installing-an-exaoperation-plug-in~1`

Needs: impl, itest

#### Listing Installed Plug-ins
`dsn~listing-plug-ins~1`

The `ExaOperationEmulator` provides a list of installed plug-in packages by name.

Covers:

* `req~listing-installed-exaoperation-plug-ins~1`

Needs: impl, itest

#### Installing Plug-ins
`dsn~installing-plug-ins~1`

The `Plugin` calls the script "`install`" from the extracted plug-in contents to complete the installation process.

Covers:

* `req~calling-exaoperation-plugin-functions~1`

Needs: impl, itest

#### Uninstalling Plug-ins
`dsn~uninstalling-plug-ins~1`

The `Plugin` calls the script "`uninstall`" from the extracted plug-in contents to uninstall a previously installed plug-in.

Covers:

* `req~calling-exaoperation-plugin-functions~1`

Needs: impl, itest

#### Starting Plug-ins
`dsn~starting-plug-ins~1`

The `Plugin` calls the script "`start`" from the extracted plug-in contents to start services provided by the plug-in.

Covers:

* `req~calling-exaoperation-plugin-functions~1`

Needs: impl, itest

#### Stopping Plug-ins
`dsn~stopping-plug-ins~1`

The `Plugin` calls the script "`stop`" from the extracted plug-in contents to stop services provided by the plug-in.

Covers:

* `req~calling-exaoperation-plugin-functions~1`

Needs: impl, itest

#### Restarting Plug-ins
`dsn~restarting-plug-ins~1`

The `Plugin` calls the script "`restart`" from the extracted plug-in contents to restart running services provided by the plug-in.

Covers:

* `req~calling-exaoperation-plugin-functions~1`

Needs: impl, itest

#### Getting the Plug-in's Status
`dsn~getting-the-plug-ins-status~1`

The `Plugin` calls the script "`status`" from the extracted plug-in contents to obtain the status of the services provided by the plug-in.

Covers:

* `req~calling-exaoperation-plugin-functions~1`

Needs: impl, itest

# Cross-cutting Concerns

# Design Decisions

## How do we Validate That Objects on BucketFS are Ready to Use?

BucketFS is a distributed filesystem with an HTTP interface. When users upload objects to a Bucket, it takes a while until they are really usable.
The reason are various asynchronous processes an object has to go through, like node synchronization and extraction of archives.

In integration tests run with the ETC, this is important, because reliable tests require objects to be available completely after they are uploaded.

### Alternatives considered

1. Using the `.dest` directory and access timestamps to check for synchronization. We dismissed this idea for two reasons.
   First this variant is too tightly coupled with the current BucketFS implementation.
   Second the layout of the `.dest` directory is very complex.

2. Checking via HTTP `GET`. Unfortunately this variant is not reliable.

### Decisions

We decided to base the check on entries in the BucketFS log. A matching log entry detected _after_ an upload proves that the object is usable.
The biggest downside of this approach is that the timestamps only have a resolution of a second.

#### Validating BucketFS Object Synchronization via the BucktFS log
`dsn~validating-bucketfs-object-synchronization-via-the-bucketfs-log~1`

The `Bucket` uses the `LogPatternDetector` to check the BucketFS log for messages that confirm object synchronization.

Covers:

* `req~waiting-for-bucket-content-synchronization`

Needs: impl, itest

#### BucketFS Object Overwrite Throttle
`dsn~bucketfs-object-overwrite-throttle~1`

The `Bucket` delays subsequent uploads to the same path in a bucket so that the upload speed does not exceed the log entry resolution.

Comment:

The logs have a timestamp resolution of a second. That is why we delay a subsequent upload to the same path so that it starts after the next second.

Covers:

* `req~waiting-for-bucket-content-synchronization`

Needs: impl, itest

# Quality Scenarios

# Risks
