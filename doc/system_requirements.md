# System Requirement Specification Exasol Test Container

## Introduction

Exasol Test Container (short "ETC") is an extension of the `testcontainers` framework (see (testcontainers.org)[https://testcontainers.org)). It add Exasol's specific features like reading from and writing to BucketFS.

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

The following list gives you an overview of terms and abbreviations commonly used in OFT documents.

* Container: (Docker container)[https://docs.docker.com/glossary/#container]
* Docker: Container-based virtualization framework
* Image: (Docker image)[https://docs.docker.com/glossary/#image]

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

## Functional Requirements

In this section lists functional requirements from the user's perspective. The requirements are grouped by feature where they belong to a single feature.

### Docker-based Exasol Instance

#### Exasol Docker Image Download
`req~exasol-docker-image-download~1`

ETC downloads the Exasol Docker image unless it is already present in Docker's local image cache.

Rationale:

Where and how to acquire the docker image necessary for a test is an infrastructure detail that is not relevant for an integration test. Therefore there is no value in forcing the integrators to deal with the image acquisition.

Covers:

* [feat~docker-based-exasol-instance~1](#docker-based-exasol-instance)

Needs: dsn

#### Docker Container Control
`req~docker-container-control~1`

ETC lets integrators control the docker container programmatically. 

Covers:
* [feat~docker-based-exasol-instance~1](#docker-based-exasol-instance)

Needs: dsn

#### Container Starts With Test
`req~container-starts-with-test~1`

The Exasol container starts together with the integration test.

Rationale:

This way the container instance is available in the test case cases automatically

Covers:

* [feat~docker-based-exasol-instance~1](#docker-based-exasol-instance)

Needs: dsn

#### Container Ready Check
`req~container-ready-check~1`

The Exasol container checks when the container and the included services are ready to be used.

Rationale:

Integration tests rely on the tested services being completely initialized in order to successfully run.

Covers:

* [feat~docker-based-exasol-instance~1](#docker-based-exasol-instance)

Needs: dsn

### Database Access

#### JDBC Connection With Administrator Privileges
`req~jdbc-connection-with-administrator-privileges~1`

ETC provides a JDBC connection for the administrator (`SYS`) to the Exasol database inside the running container.

Rationale:

The database connection is the central interface almost all integration test need. The administrator connection is necessary in integration tests to prepare the database for the test.

Covers:

* [feat~database-access~1](#database-access)

Needs: dsn

#### JDBC Connection for Username and Password
`req~jdbc-connection-for-username-and-password~1`

Upon request ETC creates a JDBC connection for a given combination of database user and password.

Rationale:

Besides the administrator account integrators often need to test with other user accounts in order to make sure that they implement permission handling correctly.

Covers:

* [feat~database-access~1](#database-access)

Needs: dsn

### BucketFS Access

#### Bucket Content Listing
`req~bucket-content-listing~1`

ETC lists the contents of a bucket in BucketFS.

Rationale:

This is useful in integration test in order to determine whether required files in a bucket exist.

Covers:

* [feat~bucketfs-access~1](#bucketfs-access)

Needs: dsn

#### Uploading a File to BucketFS
`req~uploading-a-file-to-bucketfs~1`

ETC uploads a file from a locally accessible filesystem to a bucket.

Rationale:

This allows uploading data or UDF scripts to buckets.

Covers:

* [feat~bucketfs-access~1](#bucketfs-access)

Needs: dsn

#### Uploading Text to a File in BucketFS
`req~uploading-text-to-a-file-in-bucketfs~1`

ETC uploads text (aka. a "string") to a file inside a bucket.

Rationale:

Some tests require dynamically generated configuration data in files inside buckets. This requirement allows creating the configuration in the bucket without having to create a local file first.

Covers:

* [feat~bucketfs-access~1](#bucketfs-access)

Needs: dsn

#### Bucket Authentication
`req~bucket-authentication~1`

ETC handles the authentication against the buckets automatically.

Rationale:

Unless a test is about an 3rd-party software that accesses BucketFS from the outside, providing bucket credentials is unnecessary overhead in integration tests.

Covers:

* [feat~bucketfs-access~1](#bucketfs-access)


Needs: dsn