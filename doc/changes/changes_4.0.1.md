# Exasol Test Containers 4.0.1, released 2021-07-29

Code name: Allow dots in release candidate suffix

## Summary

Release 4.0.1 contains a small update that allows using dots as separators for release candidate versions.

We also disabled the `withReuse()` feature for Exasol versions below 7, where it did not work reliably.

## Features

* #152: Version parser now accepts suffixes starting with a dot

## Dependency Updates

### Compile Dependency Updates

* Updated `com.exasol:bucketfs-java:2.0.1` to `2.1.0`
* Updated `com.exasol:database-cleaner:1.0.0` to `1.0.1`
