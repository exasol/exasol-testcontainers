# Test Containers for Exasol on Docker 7.1.5, released 2025-05-05

Code name: CVE-2024-55551 and user guide update

## Summary

CVE-2024-55551 was fixed in the Exasol driver 24.2.1, but unfortunately the OSSIndex does not reflect that.
We added an exception in the`pom.xml` for this CVE since this project uses 25.2.3.

We also updated the user guide to point to Maven central for getting the JDBC driver. This was still pointing to Exasol's own artifactory.

## Refactoring

* OSSIndex exception for CVE-2024-55551 (PR-#271)

## Dependency Updates

### Runtime Dependency Updates

* Updated `com.exasol:exasol-jdbc:25.2.2` to `25.2.3`

### Plugin Dependency Updates

* Updated `com.exasol:project-keeper-maven-plugin:4.5.0` to `5.0.1`
