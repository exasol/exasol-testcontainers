# Test Containers for Exasol on Docker 7.2.2, released 2025-12-12

Code name: Relax rules for custom image names

## Summary

This release relaxes the rules for custom image names. You can now use images with a `_` separating image name and version, e.g. `exadockerci4/docker-db:2025.1.8_dev_java_slc_only`.

## Features

* #283: Relaxed rules for custom image names

## Dependency Updates

### Plugin Dependency Updates

* Updated `org.codehaus.mojo:versions-maven-plugin:2.19.1` to `2.20.1`
