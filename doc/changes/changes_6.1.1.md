# Test containers for Exasol on Docker 6.1.1, released 2022-02-18

Code name: Maven deployment fix

## Summary

Version 6.1.1 rolls back the `nexus-staging-maven-plugin` from version 1.6.10 to 1.6.8, since the newer version has a bug that prevents deployment on Maven Central.

We also removed log message that confused users explaining that a TLS certificat did not yet exist.

## Bugfixes

* 186: Removed confusing log message.
* 189: Rolled back to `nexus-staging-maven-plugin` 1.6.8

## Dependency Updates

### Plugin Dependency Updates

* Updated `org.sonatype.plugins:nexus-staging-maven-plugin:1.6.10` to `1.6.8`
