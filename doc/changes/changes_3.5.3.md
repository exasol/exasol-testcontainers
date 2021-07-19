# Exasol Test Containers 3.5.3, released 2021-05-14

Code name: Fixed EXAConf parsing

## Summary

In this release we fixed a bug that lead to a crash when the root password was set in the EXAConf.

## Bugfixes

* #136: Fixed parsing of EXAConfig with root password set

## Dependency Updates

### Plugin Dependency Updates

* Updated `com.exasol:project-keeper-maven-plugin:0.6.1` to `0.10.0`
