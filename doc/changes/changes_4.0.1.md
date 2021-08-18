# Exasol Test Containers 4.0.1, released 2021-08-18

Code name: Allow dots in release candidate suffix

## Summary

Release 4.0.1 contains a small update that allows using dots as separators for release candidate versions.

We also disabled the `withReuse()` feature for Exasol versions below 7, where it did not work reliably.

When waiting for a database connection ETC is now smarter and also produces better diagnostic messages in case of a timeout.

Note that the database driver now by default establishes a TLS connection. At the moment ETC disables the certificate check because the certificates in the container are self-signed auto-generated. While skipping the check is good enough for a local test, this should never be done in a production environment. Later versions of ETC will feature automatic certificate extraction.

## Features

* #152: Version parser now accepts suffixes starting with a dot
* #154: Improved waiting strategies for UDF container and database connection (for compatibility with Exasol 7.1)

## Dependency Updates

### Compile Dependency Updates

* Updated `com.exasol:bucketfs-java:2.0.1` to `2.1.0`
* Updated `com.exasol:database-cleaner:1.0.0` to `1.0.1`