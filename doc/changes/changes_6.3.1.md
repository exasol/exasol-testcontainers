# Test containers for Exasol on Docker 6.3.1, released 2022-11-02

Code name: Create directory for SSH keys if missing

## Summary

With `exasol-testcontainers` 6.3.0 we introduced SSH access into the Exasol instance that runs in the Docker container as preparation for Exasol 8, where `docker exec` is disabled. The testcontainer create temporary SSH credentials for a test run that are stored in a file during the test.

You can now configure the directory where the temporary credentials are stored using the `withTemporaryCredentialsDirectory` setter when building the `ExasolContainer`. Also, the ETC automatically creates the directory if it does not exist.

## Features

* #206: Auto-create directory for SSH keys

