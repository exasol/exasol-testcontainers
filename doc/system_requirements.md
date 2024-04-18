# System Requirement Specification Exasol Test Container

## Introduction

Exasol Test Container (short "ETC") is an extension of the `testcontainers` framework, see [testcontainers.org](https://testcontainers.org). It add Exasol's specific features like reading from and writing to BucketFS.

## About This Document

### Target Audience

The target audience are end-users, requirement engineers, software designers and quality assurance. See section ["Stakeholders"](#stakeholders) for more details.

### Goal

The goal of Exasol's test container is to provide a plug-in for established test frameworks that allows creating integration tests with minimal boiler-plate code.

### Quality Goals

ETC's main quality goals are in descending order of importance:

1. Compact test code
1. Readable test code

## Stakeholders

### Integrators

Integrators integrate their solution with Exasol. To test this, they need a framework that deals with the setup and life-cycle of an Exasol instance.

### Terms and Abbreviations

The following list gives you an overview of terms and abbreviations commonly used in ETC documents.

* Container: [Docker container](https://docs.docker.com/glossary/#container)
* Docker: Container-based virtualization framework
* Image: [Docker image](https://docs.docker.com/glossary/#image)

## Features

Features are the highest level requirements in this document that describe the main functionality of ETC.

### Docker-based Exasol Instance
`feat~docker-based-exasol-instance~1`

ETC provides an Exasol instance running on Docker.

Needs: req

### Database Access
`feat~database-access~1`

ETC provides access to a running Exasol database.

Needs: req

### BucketFS Access
`feat~bucketfs-access~1`

ETC provides access to the BucketFS service(s) of the Exasol database.

Needs: req

### Install Custom Script Language Containers (SLC)
`feat~install-custom-slc~1`

ETC allows installing custom SLCs during.

Needs: req

### Log Access
`feat~log-access~1`

ETC provides access to the logs of the Exasol instance.

Needs: req

### Driver Management
`feat~driver-management~1`

ETC manages drivers for external data sources.

Needs: req

### EXAoperation Simulation
`feat~exaoperation-simulation~1`

ETC simulates selected functions of EXAoperation.

Needs: req

## Functional Requirements

In this section lists functional requirements from the user's perspective. The requirements are grouped by feature where they belong to a single feature.

### Docker-based Exasol Instance

#### Exasol Docker Image Download
`req~exasol-docker-image-download~1`

ETC downloads the Exasol Docker image unless it is already present in Docker's local image cache.

Rationale:

Where and how to acquire the docker image necessary for a test is an infrastructure detail that is not relevant for an integration test. Therefore there is no value in forcing the integrators to deal with the image acquisition.

Covers:

* [`feat~docker-based-exasol-instance~1`](#docker-based-exasol-instance)

Needs: dsn

#### Docker Container Control
`req~docker-container-control~1`

ETC lets integrators control the docker container programmatically.

Covers:
* [`feat~docker-based-exasol-instance~1`](#docker-based-exasol-instance)

Needs: dsn

#### Container Starts With Test
`req~container-starts-with-test~1`

The Exasol container starts together with the integration test.

Rationale:

This way the container instance is available in the test case cases automatically

Covers:

* [`feat~docker-based-exasol-instance~1`](#docker-based-exasol-instance)

Needs: dsn

#### Optional Container Reuse
`req~reuse-container~1`

Integrators can decide to reuse the container and keep the container running across the tests.

Rationale:

Reusing the container improves the productivity since integrators do not have to wait for the container start up.

Covers:

* [`feat~docker-based-exasol-instance~1`](#docker-based-exasol-instance)

Needs: dsn

#### Automatic Database Cleanup With Reused Containers
`req~automatic-database-cleanup-with-reused-containers~1`

If the reuse is enabled ETC purges the container before use and does not stop the container after the tests.

Rationale:

This ensures a clean environment when running independent tests.

Covers:

* [`feat~docker-based-exasol-instance~1`](#docker-based-exasol-instance)

Needs: dsn

#### Container Ready Check
`req~container-ready-check~1`

The Exasol container checks when the container and the included services are ready to be used.

Rationale:

Integration tests rely on the tested services being completely initialized in order to successfully run.

Covers:

* [`feat~docker-based-exasol-instance~1`](#docker-based-exasol-instance)

Needs: dsn

#### Defining Required Optional Services
`req~defining-required-optional-services~1`

Integrators can decide whether or not they need one or more of the following services to be available for their tests:

1. BucketFS
2. UDFs

Rationale:

In case of test that do not use UDFs or bucket objects, waiting for the container to become ready wastes unnecessary time.

Covers:

* [`feat~docker-based-exasol-instance~1`](#docker-based-exasol-instance)

Needs: dsn

#### Matrix Testing With Different Docker Images
`req~matrix-testing-with-different-docker-images~1`

Integrators can run the same build that uses ETC for integration tests against multiple versions of Exasol.

Rationale:

Matrix tests are required if integrators want to ensure backward compatibility of their product. They also allow testing against variants, e.g. Exasol variants with different feature sets.

Covers:

* [`feat~docker-based-exasol-instance~1`](#docker-based-exasol-instance)

Needs: dsn

#### Shortened Docker Image References
`req~shortened-docker-image-references~2`

Users can reference an Exasol Docker image names of the following kind:

* `<major>[-suffix][-d<docker-image-revision>]`
* `<major>.<minor>[-suffix][-d<docker-image-revision>]`
* `<major>.<minor>.<fix>[-suffix][-d<docker-image-revision>]`
* All of the above prefixed with `docker-db:` or `exasol/docker-db:`.

Optional parts are indicated by square brackets.

Covers:

* [`feat~docker-based-exasol-instance~1`](#docker-based-exasol-instance)

Needs: dsn

#### Reading the Host IP Address
`req~reading-the-host-ip-address~1`

Integrators can read the IP address of the host the container is running on.

Rationale:

This is useful for reverse socket connections, the kind of which debugging or profiling agents often use.

Covers:

* [`feat~docker-based-exasol-instance~1`](#docker-based-exasol-instance)

Needs: dsn

#### Configuring the Directory for Temporary Credentials
`req~configuring-the-directory-for-temporary-credentials~1`

The ETC allows users to configure a directory where temporary credentials generated by the test container are stored.

Rationale:

Temporary credentials are required in some combinations of ETC and Exasol. For example to access the container via SSH. Configuring the directory allows users to adapt where those credentials should go.

Covers:

* [`feat~docker-based-exasol-instance~1`](#docker-based-exasol-instance)

Needs: dsn

#### Retrieving Support Information

Exasol contains a utility that collects all relevant data required for investigating database problems. This utility creates an archive that is standardized accross Exasol versions.

The support archive contains:

* system information
* server logs
* configuration files
* core-dumps (if any)

##### Retrieving the Support Archive via API
`req~retrieving-the-support-archive-via-api~1`

Integrators can configure ETC to produce a standard support package when the dockerized database shuts down via the ETC API.

Rationale:

This is useful in cases where single test cases should produce the standard support archive.

Covers:

* [`feat~docker-based-exasol-instance~1`](#docker-based-exasol-instance)

Needs: dsn

##### Retrieving the Support Archive via System Properties
`req~retrieving-the-support-archive-via-system-properties~1`

Integrators can configure ETC to produce a standard support package when the dockerized database shuts down via Java properties.

Rationale:

This is targeted at CI builds as it decouples test case definition and decision over whether to collect the support information.

Covers:

* [`feat~docker-based-exasol-instance~1`](#docker-based-exasol-instance)

Needs: dsn

##### Exit-dependent Support Archive Generation
`req~exit-dependent-support-archive-generation~1`

Integrators can choose whether the support archive should be generated on

* exits with errors
* successful exits
* both
* none (default)

Rationale:

During regular tests integrators usually don't need the archive. In CI builds the runs with errors are the interesting ones. And in performance tests all information are relevant.

Covers:

* [`feat~docker-based-exasol-instance~1`](#docker-based-exasol-instance)

Needs: dsn

### Database Access

#### JDBC Connection With Administrator Privileges
`req~jdbc-connection-with-administrator-privileges~1`

ETC provides a JDBC connection for the administrator (`SYS`) to the Exasol database inside the running container.

Rationale:

The database connection is the central interface needed by almost all integration tests. The administrator connection is necessary in integration tests to prepare the database for the test.

Covers:

* [`feat~database-access~1`](#database-access)

Needs: dsn

#### JDBC Connection for Username and Password
`req~jdbc-connection-for-username-and-password~1`

Upon request ETC creates a JDBC connection for a given combination of database user and password.

Rationale:

Besides the administrator account integrators often need to test with other user accounts in order to make sure that they implement permission handling correctly.

Covers:

* [`feat~database-access~1`](#database-access)

Needs: dsn

#### ExaLoader Between Two Containers
`req~exaloader-between-two-containers~1`

Given two running Exasol instances each inside a test container, the ExaLoader on one is able to import data from the other database.

Rationale:

This allows testing scenarios that use the ExaLoader (i.e. `IMPORT` statements).

Covers:

* [`feat~database-access~1`](#database-access)

Needs: dsn

#### Starting and Stopping the Database
`req~starting-and-stopping-the-database~1`

Integrators can start and stop an Exasol database service inside the test container.

Rationale:

This allows testing the fault tolerance of software depending on an Exasol database.

Covers:

* [`feat~database-access~1`](#database-access)

Needs: dsn

### BucketFS Access

#### Bucket Content Listing
`req~bucket-content-listing~1`

ETC lists the contents of a bucket in BucketFS.

Rationale:

This is useful in integration test in order to determine whether required files in a bucket exist.

Covers:

* [`feat~bucketfs-access~1`](#bucketfs-access)

Needs: dsn

#### Uploading a File to BucketFS
`req~uploading-a-file-to-bucketfs~1`

ETC uploads a file from a locally accessible filesystem to a bucket.

Rationale:

This allows uploading data or UDF scripts to buckets.

Covers:

* [`feat~bucketfs-access~1`](#bucketfs-access)

Needs: dsn

#### Uploading Text to a File in BucketFS
`req~uploading-text-to-a-file-in-bucketfs~1`

ETC uploads text (aka. a "string") to a file inside a bucket.

Rationale:

Some tests require dynamically generated configuration data in files inside buckets. This requirement allows creating the configuration in the bucket without having to create a local file first.

Covers:

* [`feat~bucketfs-access~1`](#bucketfs-access)

Needs: dsn

#### Uploading InputStream to a File in BucketFS
`req~uploading-input-stream-to-a-file-in-bucketfs~1`

ETC uploads the contents of an InputStream to a file inside a bucket.

Rationale:

Some tests load the content from resources using `getResourceAsStream()`.

Covers:

* [`feat~bucketfs-access~1`](#bucketfs-access)

Needs: dsn

#### Downloading a File from BucketFS
`req~downloading-a-file-from-bucketfs~1`

ETC downloads a file from a bucket to a locally accessible filesystem.

Rationale:

This allows downloading files like e.g. logs from buckets.

Covers:

* [`feat~bucketfs-access~1`](#bucketfs-access)

Needs: dsn

#### Bucket Authentication
`req~bucket-authentication~1`

ETC handles the authentication against the buckets automatically.

Rationale:

Unless a test is about an 3rd-party software that accesses BucketFS from the outside, providing bucket credentials is unnecessary overhead in integration tests.

Covers:

* [`feat~bucketfs-access~1`](#bucketfs-access)


Needs: dsn

#### Waiting for Bucket Content Synchronization
`req~waiting-for-bucket-content-synchronization~1`

ETC allows integrators to wait for bucket contents to be synchronized on a single node after uploading a file.

Rationale:

Files uploaded to Bucket FS are not immediately usable due to internal synchronization mechanisms. For integration tests it is necessary that integrators can rely on the file to be available in a bucket before running tests that depend on it.

Covers:

* [`feat~bucketfs-access~1`](#bucketfs-access)

Needs: dsn

### Install Custom SLC
`req~install-custom-slc~1`

ETC lets Integrators install custom SLCs.

Rationale:

Integrators want to test their integration with a custom Script Language Container (SLC). This is necessary in the following cases:
* Integrators want to use a later Java version than supported by the default SLC that comes with the Exasol Docker container
* Integrators want to use additional Python libraries and build a custom SLC with the required libraries

Covers:
* [`feat~install-custom-slc~1`](#install-custom-script-language-containers-slc)

Needs: dsn

### Log Access

During the installation process and while running the Exasol installation inside the Docker container produces a lot of log files. Integrators need access to those logs in order to see what is going on inside the Exasol platform.

#### Reading Log Files
`req~reading-log-files~1`

ETC lets Integrators read the Exasol log files.

Covers:

* [`feat~log-access~1`](#log-access)

Needs: dsn

### Driver Management

The Exasol database supports installation of drivers for external data sources through [BucketFS](#bucketfs-access). Installation requires Integrators to follow a [convention for storing and registering those drivers](https://github.com/exasol/docker-db#user-content-installing-custom-jdbc-drivers).

ETC abstracts the conventions and the corresponding installation and uninstallation process, allowing to add and remove drivers programmatically with ease.

#### Installing a JDBC Driver
`req~installing-a-jdbc-driver~1`

ETC lets Integrators install a JDBC driver from a file on the host filesystem.

Rationale:

Uploading a driver to BucketFS and registering it requires a lot of repetitive boilerplate code. Removing that boilerplate makes tests more readable, compact and less error-prone.

Covers:

* [`feat~driver-management~1`](#driver-management)

Needs: dsn

### EXAoperation Simulation

The Exasol test container is based on Exasol's `docker-db` which does not include EXAoperation. That being said some integration tests require a subset of the EXAoperation functions in order to be executable.

#### Installing an EXAoperation Plug-in
`req~installing-an-exaoperation-plug-in~1`

ETC allows installing an EXAoperation plug-in from a plug-in package file.

Rationale:

If the software under test is an EXAoperation plug-in, ETC takes over the installation in the absence of EXAoperation.

Covers:

* [`feat~exaoperation-simulation~1`](#exaoperation-simulation)

Needs: dsn

#### Listing Installed EXAoperation Plug-ins
`req~listing-installed-exaoperation-plug-ins~1`

ETC allows listing the names of installed EXAoperation plug-ins.

Rationale:

To be resilient against minor naming changes (like version numbers), a test should be able to find a plugin based on local rules (eg. regular expressions).

Covers:

* [`feat~exaoperation-simulation~1`](#exaoperation-simulation)

Needs: dsn

#### Calling Plugin Functions
`req~calling-exaoperation-plugin-functions~1`

ETC allows calling functions of a previously installed EXAoperation plug-in.

Rationale:

After all, that's why we installed the plug-in in the first place: To be able to call methods and get back information on execution status.

Covers:

* [`feat~exaoperation-simulation~1`](#exaoperation-simulation)

Needs: dsn

### Workarounds

Workarounds are mitigations for bugs that exist outside of ETC.

#### Log Rotation Workaround
`req~log-rotation-workaround~1`

ETC mitigates the a bug present in Exasol 7.0.x and below that prevents log rotation from succeeding.

Rationale:

This bug causes the logs to not be rotated correctly when the Cron job starts the rotation. By default this happens after 40 minutes.

Needs: dsn
