# Exasol Test Containers 4.0.1, released 2021-07-29

Code name: Allow dots in release candidate suffix

## Summary

Release 4.0.1 contains a small update that allows using dots as separators for release candidate versions.

## Breaking Changes

* `bucket-fs-java` was updated to 2.0.1, which changed the method signature or some methods in order to offer better exceptions.
* Deprecated methods were removed from `ExasolDockerImageReference`.

## Features

* #152: Version parser now accepts suffixes starting with a dot