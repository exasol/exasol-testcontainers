# Exasol Test Containers 3.5.1, released 2021-02-18

Code name: Fixed database purging

## Summary

In order to still guarantee isolation between tests, the testcontainers purge the database on each start when reusing a container. Purging means that they delete all existing database objects.

This purging mechanism had a bug that made the container startup fail, when the name of a database object was not unique over all schemas. This release fixes this bug.

## Bug fixes

* #124: Fixed database purging