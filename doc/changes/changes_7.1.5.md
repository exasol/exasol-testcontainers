# Test Containers for Exasol on Docker 7.1.5, released 2025-05-05

Code name: CVE-2024-55551 and user guide update

## Summary

CVE-2024-55551 was fixed in the Exasol driver 24.2.1, but unfortunately the OSSIndex does not reflect that.
We added an exception in the `pom.xml` for this CVE since this project uses 25.2.3.

We also updated the user guide to point to Maven central for getting the JDBC driver. This was still pointing to Exasol's own artifactory.

## Refactoring

* OSSIndex exception for CVE-2024-55551 (PR-#271)

## Dependency Updates

### Plugin Dependency Updates

* Updated `com.exasol:project-keeper-maven-plugin:4.5.0` to `5.0.1`
* Added `io.github.git-commit-id:git-commit-id-maven-plugin:9.0.1`
* Removed `io.github.zlika:reproducible-build-maven-plugin:0.17`
* Added `org.apache.maven.plugins:maven-artifact-plugin:3.6.0`
* Updated `org.apache.maven.plugins:maven-clean-plugin:3.4.0` to `3.4.1`
* Updated `org.apache.maven.plugins:maven-compiler-plugin:3.13.0` to `3.14.0`
* Updated `org.apache.maven.plugins:maven-deploy-plugin:3.1.3` to `3.1.4`
* Updated `org.apache.maven.plugins:maven-install-plugin:3.1.3` to `3.1.4`
* Updated `org.apache.maven.plugins:maven-javadoc-plugin:3.11.1` to `3.11.2`
* Updated `org.codehaus.mojo:flatten-maven-plugin:1.6.0` to `1.7.0`
