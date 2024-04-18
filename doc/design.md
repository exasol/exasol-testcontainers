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

## Alternative to Docker Exec
`const~alternative-to-docker-exec~1`

Usually `docker exec` can be used to connect to a docker container in order to execute shell commands or to copy files. Depending on the detailed process the Exasol Docker container was built with it might not be possible to use `docker exec`. This requires to first detect if `docker exec` is possible and second to provide an alternative method for the same purpose.

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

## `ExasolDriverManager`

The `ExasolDriverManager` is responsible for installing and uninstalling drivers for external data sources.

## `HostIpDetector`

The `HostIpDetector` detects the IP address of the host the container is running on.

## `SupportInformationRetriever`

The `SupportInformationRetriever` module provides access to cluster logs, configuration and core-dumps.

## `ScriptLanguageContainerInstaller`

The `ScriptLanguageContainerInstaller` is responsible for installing custom Script Language Containers (SLC).

# Runtime

This section describes the runtime behavior of the software.

## Docker-based Exasol Instance

### `testcontainers` Framework Controls Docker Image Download

`dsn~testcontainer-framework-controls-docker-image-download~1`

The `testcontainers` framework uses Docker's own facilities to download the Exasol docker image in case it is not in the local Docker cache.

Covers:
* [`req~exasol-docker-image-download~1`](system_requirements.md#exasol-docker-image-download)

Needs: external

### `ExasolContainer` Controls Docker Container

`dsn~exasol-container-controls-docker-container~1`

The `ExasolContainer` controls the underlying Exasol Docker container through the `testcontainers` framework.

Covers:
* [`req~docker-container-control~1`](system_requirements.md#docker-container-control)

Needs: impl, itest

### `ExasolContainer` Starts with Test

`dsn~exasol-container-starts-with-test~1`

JUnit starts the Exasol Container before the test cases in a test class.

Covers:
* [`req~container-starts-with-test~1`](system_requirements.md#container-starts-with-test)

Needs: itest

### Optional `ExasolContainer` Reuse

`dsn~control-reuse~1`

The `ExasolContainer` offers control to reuse containers using a switch in code and a second switch on their local machine (in `~/.testcontainers.properties`)

Covers:
* [`req~reuse-container~1`](system_requirements.md#optional-container-reuse)

### Keep Container Running if Reuse is Enabled

`dsn~keep-container-running-if-reuse~1`

If reuse is enabled, ETC does not stop the container after the tests are finished.

Covers:
* [`req~reuse-container~1`](system_requirements.md#optional-container-reuse)

Needs: impl, itest

### Purging

`dsn~purging~1`

The `ExasolContainer` purges the database when reuse is enabled.

Comment:

This means the `ExasolContainer` deletes all database objects such as users, roles and connections.

Covers:
* [`req~automatic-database-cleanup-with-reused-containers~1`](system_requirements.md#automatic-database-cleanup-with-reused-containers)

Needs: impl, itest

### `ExasolContainer` Ready Criteria

`dsn~exasol-container-ready-criteria~3`

The `ExasolContainer` declares itself ready to be used after the following criteria are fulfilled:

* SQL statements can be issued via the JDBC interface
* BucketFS service is ready
* UDF language container is extracted completely

Covers:
* [`req~container-ready-check~1`](system_requirements.md#container-ready-check)

Needs: impl, itest

### Defining Required Optional Services

`dsn~defining-required-optional-service~1`

The `ExasolContainer` offers an option that controls which services the user requires. The ETC waits for those services to be ready.

Covers:
* [`req~defining-required-optional-services~1`](system_requirements.md#defining-required-optional-services)

Needs: impl, itest

### `ExasolContainer` Uses Privileged Mode
`dsn~exasol-container-uses-privileged-mode~1`

The `ExasolContainer` tells the `testcontainers` framework to start the Docker container in privileged mode.

Covers:
* [`const~exasol-docker-container-requires-privileged-mode~1`](#exasol-docker-container-requires-privileged-mode)

Needs: impl, itest

### Alternative to Docker Exec

#### Detect If Docker Exec is Possible
`dsn~detect-if-docker-exec-is-possible~1`

ETC detects whether `docker exec` can be used by probing for cluster configuration file `/exa/etc/EXAConf`. If ETC cannot find this file then ETC assumes that `docker exec` cannot be used and chooses [Access via SSH](#access-via-ssh) as alternative.

Covers:
* [`const~alternative-to-docker-exec~1`](#alternative-to-docker-exec)

Needs: impl, utest

#### Access via SSH
`dsn~access-via-ssh~1`

If `docker exec` cannot be used then ETC uses SSH to access the docker container.

Covers:
* [`const~alternative-to-docker-exec~1`](#alternative-to-docker-exec)

Needs: impl, utest

#### Configuring the Directory for Temporary Credentials
`dsn~configuring-the-directory-for-temporary-credentials~1`

ETC offers a configuration option to set the directory for temporary credentials.

Covers:
* `req~configuring-the-directory-for-temporary-credentials~1`

Needs: impl, utest

#### Auto-create Directory for Temporary Credentials
`dsn~auto-create-directory-for-temporary-credentials~1`

ETC automatically creates the directory for the temporary credentials (e.g. temporary SSH keys) if it is missing.

Rationale:

This avoids unnecessary startup failure that would otherwise happen if the keys cannot be stored.

Covers:
* [`const~alternative-to-docker-exec~1`](#alternative-to-docker-exec)

Needs: impl, utest

### Override Docker Image via Java Property

`dsn~override-docker-image-via-java-property~1`

If the Java property `com.exasol.dockerdb.image` is set, it overrides the docker image given in the code that creates the container instance.

Rationale:

This allows running the same build with unaltered code multiple times while mutating the docker image used in the test.

Covers:
* [`req~matrix-testing-with-different-docker-images~1`](system_requirements.md#matrix-testing-with-different-docker-images)

Needs: impl, itest

### Shortened Docker Image References

`dsn~shortened-docker-image-references~2`

The `ExasolDockerImageReference` can be constructed with the following forms of shortened image references as parameter:

* `<major>[-suffix][-d<docker-image-revision>]`
* `<major>.<minor>[-suffix][-d<docker-image-revision>]`
* `<major>.<minor>.<fix>[-suffix][-d<docker-image-revision>]`
* All of the above prefixed with `docker-db:` or `exasol/docker-db:`.

Where `major`, `minor`, `fix` and `docker-image-revision` can only contain numbers.
Optional parts are indicated by square brackets.

Covers:
* [`req~shortened-docker-image-references~2`](system_requirements.md#shortened-docker-image-references)

Needs: impl, utest

### Host IP Address Detection

`dsn~host-ip-address-detection~1`

The `HostIpDetector` scans the Docker network for the Gateway address and uses this as host address.

Covers:
* [`req~reading-the-host-ip-address~1`](system_requirements.md#reading-the-host-ip-address)

Needs: impl, itest

### Support Archive Retrieval

To make sure that the support packages have the same content, we us the `exasupport` utility that ships with all Exasol variants.
`exasupport` creates a standardized archive containing all relevant information for investigating problems with an Exasol cluster.

#### Configure `SupportInformationRetriever` via API
`dsn~configure-support-information-retriever-via-api~1`

The `SupportInformationRetriever` provides an API to retrieve the support archive as produced by the `exasupport` utility.

Covers:
* [`req~retrieving-the-support-archive-via-api~1`](system_requirements.md#retrieving-the-support-archive-via-api)

Needs: impl, itest

#### Configure `SupportInformationRetriever` via system Properties
`dsn~configure-support-information-retriever-via-system-properties~1`

The `SupportInformationRetriever` provides an API to retrieve the support archive as produced by the `exasupport` utility.

Covers:
* [`req~retrieving-the-support-archive-via-system-properties~1`](system_requirements.md#retrieving-the-support-archive-via-system-properties)

Needs: impl, itest

#### `SupportInformationRetriever` Creates Support Archive Depending on Exit Type
`dsn~support-information-retriever-creates-support-archive-depending-on-exit-type~1`

The `SupportInformationRetriever` offers a configuration option that decides whether the support archive is created at on one of the following exit types:

* Exit with error
* Exit with success
* Both
* None (default)

Covers:
* [`req~exit-dependent-support-archive-generation~1`](system_requirements.md#exit-dependent-support-archive-generation)

Needs: impl, itest

## Database Access

### `ExasolContainer` Provides a JDBC Connection for Username and Password

`dsn~exasol-container-provides-a-jdbc-connection-for-username-and-password~1`

The `ExasolContainer` can create a JDBC connection for a given combination of username and password.

Covers:
* [`req~jdbc-connection-for-username-and-password~1`](system_requirements.md#jdbc-connection-for-username-and-password)

Needs: impl, itest

### Default JDBC Connection With SYS Credentials

`dsn~default-jdbc-connection-with-sys-credentials~1`

Unless the integrator provides database user credentials with the creation of the `ExasolContainer`, the container uses the `SYS` user and its default password.

Comment:

Note that we are talking about a disposable integration test environment with non-confidential test data here. This convenience function would not be acceptable in a production environment.

Covers:
* [`req~jdbc-connection-with-administrator-privileges~1`](system_requirements.md#jdbc-connection-with-administrator-privileges)

Needs: impl, itest

### IP Address in Common Docker Network

`dsn~ip-address-in-common-docker-network~1`

ETC reports the docker-network internal IP address.

Rationale:

This is the main requirement for any Exasol service to be able to reach another service in the same Docker network. Hostname resolution via Docker network alias is not supported by [`exasol/docker-db`](https://github.com/exasol/docker-db) yet.

Covers:
* [`req~exaloader-between-two-containers~1`](system_requirements.md#exaloader-between-two-containers)

Needs: impl, itest

### `DatabaseService` stops the Database

`dsn~database-service-stops-the-database~1`

The `DatabaseService` allows users to stop the database provided by that service.

Covers:
* [`req~starting-and-stopping-the-database~1`](system_requirements.md#starting-and-stopping-the-database)

Needs: impl, itest

### `DatabaseService` starts the Database

`dsn~database-service-starts-the-database~1`

The `DatabaseService` allows users to start the database provided by that service.

Covers:
* [`req~starting-and-stopping-the-database~1`](system_requirements.md#starting-and-stopping-the-database)

Needs: impl, itest

## BucketFS Access

### Use Bucketfs-java Library for BucketFs Access

`dsn~bucket-api~1`

ETC implements a factory for Buckets of the [bucketfs-java](https://github.com/exasol/bucketfs-java/) library.

Covers:
* [`req~bucket-content-listing~1`](system_requirements.md#bucket-content-listing)
* [`req~uploading-a-file-to-bucketfs~1`](system_requirements.md#uploading-a-file-to-bucketfs)
* [`req~uploading-text-to-a-file-in-bucketfs~1`](system_requirements.md#uploading-text-to-a-file-in-bucketfs)
* [`req~uploading-input-stream-to-a-file-in-bucketfs~1`](system_requirements.md#uploading-inputstream-to-a-file-in-bucketfs)
* [`req~bucket-authentication~1`](system_requirements.md#bucket-authentication)
  req~bucket-authentication~1
* [`req~waiting-for-bucket-content-synchronization~1`](system_requirements.md#waiting-for-bucket-content-synchronization)
* [`req~downloading-a-file-from-bucketfs~1`](system_requirements.md#downloading-a-file-from-bucketfs)

Needs: impl, utest, itest

## Install Custom Script Language Containers (SLC)
`dsn~install-custom-slc~1`

The `ExasolContainer` lets integrators install custom SLCs during container startup.

Covers:
* [`req~install-custom-slc~1`](system_requirements.md#install-custom-slc)

Needs: impl, utest, itest

### Only Install SLC When Not Yet Done
`dsn~install-custom-slc.only-if-required~1`

The `ExasolContainer` checks the container status if a given SLC was already installed and skips installation.

Rationale:
In case a container is reused we want to skip installation to speed-up tests.

Needs: impl, utest, itest

### Install SLC From Local File
`dsn~install-custom-slc.local-file~1`

ETC allows installing SLCs from a local file.

Rationale:
This is useful during development when testing an SLC that was not yet released.

Needs: impl, utest

### Install SLC From a URL
`dsn~install-custom-slc.url~1`

ETC allows installing SLCs from an URL.

Rationale:
This is useful for running tests during CI with a released SLC.

Needs: impl, utest, itest

### Verify SLC Checksum
`dsn~install-custom-slc.verify-checksum~1`

ETC verifies the Sha512 checksum of SLCs downloaded from the internet.

Rationale:
This is required to ensure the integrity of the downloaded file.

Needs: impl, utest, itest

## Log Access

### Mapping the Cluster Log Directory to the Host

`dsn~mapping-the-log-directory-to-the-host~2`

The `ExasolContainer` lets integrators map the cluster log directory to a configurable directory on the host.

This feature was removed because mapping the log directory with `addFileSystemBind()` is broken and does not work any more, see [#240](https://github.com/exasol/exasol-testcontainers/issues/240).

### Clock Synchronization for Log Timestamp Correlation
`dsn~clock-synchronization~1`

ETC verifies that the clocks of the VM and the host running the Test Containers software are synchronized.

Rationale:

This allows correlating events in the ETC code to events inside the container by correlating timestamps.

Covers:
* [`req~reading-log-files~1`](system_requirements.md#reading-log-files)

Needs: impl, itest

## Driver Management

### Installing a JDBC Driver

`dsn~installing-a-jdbc-driver-from-host-filesystem~1`

The `ExasolDriverManager` lets integrators install a JDBC driver from the host's filesystem.

Covers:
* [`req~installing-a-jdbc-driver~1`](system_requirements.md#installing-a-jdbc-driver)

Needs: impl, utest, itest

## ExaOperation Emulation

EXAoperation features required for integration tests are emulated.

Interface compatibility for plug-ins means that the emulation uses the established plug-in interfaces (read "control scripts inside the plug-in").

EXAoperation's own interfaces are _not_ available. Especially there is no emulation of the XML-RPC interface. What is available though is a control object implementation of a Java interface called "EXAoperation". It is this interface that test authors can use in test code to trigger the emulated features.

#### Extracting Plug-in Packages

`dsn~extracting-plug-in-packages~1`

The `ExaOperationEmulator` unpacks plug-in packages from TAR archives with the suffix `.pgk` inside the Exasol instance.

Covers:
* [`req~installing-an-exaoperation-plug-in~1`](system_requirements.md#installing-an-exaoperation-plug-in)

Needs: impl, itest

#### Listing Installed Plug-ins

`dsn~listing-plug-ins~1`

The `ExaOperationEmulator` provides a list of installed plug-in packages by name.

Covers:
* [`req~listing-installed-exaoperation-plug-ins~1`](system_requirements.md#listing-installed-exaoperation-plug-ins)

Needs: impl, itest

#### Installing Plug-ins

`dsn~installing-plug-ins~1`

The `Plugin` calls the script "`install`" from the extracted plug-in contents to complete the installation process.

Covers:
* [`req~calling-exaoperation-plugin-functions~1`](system_requirements.md#calling-plugin-functions)

Needs: impl, itest

#### Uninstalling Plug-ins

`dsn~uninstalling-plug-ins~1`

The `Plugin` calls the script "`uninstall`" from the extracted plug-in contents to uninstall a previously installed plug-in.

Covers:
* [`req~calling-exaoperation-plugin-functions~1`](system_requirements.md#calling-plugin-functions)

Needs: impl, itest

#### Starting Plug-ins

`dsn~starting-plug-ins~1`

The `Plugin` calls the script "`start`" from the extracted plug-in contents to start services provided by the plug-in.

Covers:
* [`req~calling-exaoperation-plugin-functions~1`](system_requirements.md#calling-plugin-functions)

Needs: impl, itest

#### Stopping Plug-ins

`dsn~stopping-plug-ins~1`

The `Plugin` calls the script "`stop`" from the extracted plug-in contents to stop services provided by the plug-in.

Covers:
* [`req~calling-exaoperation-plugin-functions~1`](system_requirements.md#calling-plugin-functions)

Needs: impl, itest

#### Restarting Plug-ins

`dsn~restarting-plug-ins~1`

The `Plugin` calls the script "`restart`" from the extracted plug-in contents to restart running services provided by the plug-in.

Covers:
* [`req~calling-exaoperation-plugin-functions~1`](system_requirements.md#calling-plugin-functions)

Needs: impl, itest

#### Getting the Plug-in's Status

`dsn~getting-the-plug-ins-status~1`

The `Plugin` calls the script "`status`" from the extracted plug-in contents to obtain the status of the services provided by the plug-in.

Covers:
* [`req~calling-exaoperation-plugin-functions~1`](system_requirements.md#calling-plugin-functions)

Needs: impl, itest

## Workarounds

### WorkaroundManager Applies Multiple Workarounds

`dsn~workaround-manager-applies-multiple-of-workarounds~1`

The `WorkaroundManager` applies workarounds in the order they are registered.

Covers:
* [`req~log-rotation-workaround~1`](system_requirements.md#log-rotation-workaround)

Needs: impl, utest

### WorkaroundManager Checks Criteria

`dsn~workaround-manager-checks-criteria~1`

The `WorkaroundManager` applies a workaround if that workaround reports that its individual application criteria apply.

Covers:
* [`req~log-rotation-workaround~1`](system_requirements.md#log-rotation-workaround)

Needs: impl, utest

### Log Rotation Workaround Criteria

`dsn~log-rotation-workaround-criteria~1`

the `WorkaroundManager` applies the `LogRotationWorkaround` if the Exasol version is 7.0.x or lower.

Covers:
* [`req~log-rotation-workaround~1`](system_requirements.md#log-rotation-workaround)

Needs: impl, utest

### Log Rotation Workaround

`dsn~log-rotation-workaround~1`

The `LogRotationWorkaround` removes the BucketFS log file from the list of logs to be rotated in `/etc/cron.daily/exa-logrotate`.

Covers:
* [`req~log-rotation-workaround~1`](system_requirements.md#log-rotation-workaround)

Needs: impl, utest, itest

# Cross-cutting Concerns

# Design Decisions

## How do we Validate That Objects on BucketFS are Ready to Use?

BucketFS is a distributed filesystem with an HTTP interface. When users upload objects to a Bucket, it takes a while until they are really usable.

This is caused by various asynchronous processes an object has to go through, like node synchronization and extraction of archives.

In integration tests run with the ETC, this is important, because reliable tests require objects to be available completely after they are uploaded.

### Alternatives considered

1. Using the `.dest` directory and access timestamps to check for synchronization. We dismissed this idea for two reasons. First, this variant is too tightly coupled with the current BucketFS implementation. Second, the layout of the `.dest` directory is very complex.

2. Checking via HTTP `GET`. Unfortunately this variant is not reliable.


## How to use HTTPS Connections with Self-Signed TLS Certificates

At startup the container generates a self signed TLS certificate and uses it to encrypt database and HTTPS connections. The certificate contains an invalid hostname, that is why hostname validation will fail. To solve this we would need to inject a new certificate with correct hostname into the container.

We decided to not add this feature.

### Rationale

1. Installing a new certificate requires restarting multiple database services. Restarting the complete docker container would be a good alternative but is not supported by testcontainers-java because [docker assigns new ports after restart](https://github.com/testcontainers/testcontainers-java/issues/606).
2. Stopping and starting individual database services using `cosrm` and `cosexec` requires knowledge about database internals and creates tight coupling.
3. Modifying the startup command of the container to execute a custom script adds too much complexity.

### Workarounds

To use HTTPS connections with the self-signed certificate the user has the following options:

#### `java.net.http.HttpClient`

Set system property `jdk.internal.httpclient.disableHostnameVerificationdokumentieren` to `true`. This disables hostname verification for the complete VM and must not be used in production.

#### `javax.net.ssl.HttpsURLConnection`

Use method [setHostnameVerifier()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/javax/net/ssl/HttpsURLConnection.html#setHostnameVerifier(javax.net.ssl.HostnameVerifier)) to set a custom hostname verifier.

# Quality Scenarios

# Risks
