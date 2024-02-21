# Test Containers for Exasol on Docker 7.0.1, released 2024-02-21

Code name: Fix CVE-2024-26308 and CVE-2024-25710

## Summary

This release fixes vulnerabilities CVE-2024-25710 and CVE-2024-26308 in compile dependency `org.apache.commons:commons-compress`.

The release also updates the default Exasol DB version from 7.1.24 to 7.1.25.

## Security

* #243: Fixed CVE-2024-25710 in `org.apache.commons:commons-compress`
* #244: Fixed CVE-2024-26308 in `org.apache.commons:commons-compress`

## Dependency Updates

### Compile Dependency Updates

* Updated `org.testcontainers:jdbc:1.19.2` to `1.19.5`
* Updated `org.testcontainers:testcontainers:1.19.2` to `1.19.5`

### Runtime Dependency Updates

* Added `commons-codec:commons-codec:1.16.1`
* Added `org.apache.commons:commons-compress:1.26.0`

### Test Dependency Updates

* Updated `org.junit.jupiter:junit-jupiter-engine:5.10.1` to `5.10.2`
* Updated `org.junit.jupiter:junit-jupiter-params:5.10.1` to `5.10.2`
* Updated `org.mockito:mockito-junit-jupiter:5.7.0` to `5.10.0`
* Updated `org.testcontainers:junit-jupiter:1.19.2` to `1.19.5`

### Plugin Dependency Updates

* Updated `com.exasol:project-keeper-maven-plugin:2.9.16` to `3.0.1`
* Updated `org.apache.maven.plugins:maven-failsafe-plugin:3.2.2` to `3.2.3`
* Updated `org.apache.maven.plugins:maven-javadoc-plugin:3.6.2` to `3.6.3`
* Updated `org.apache.maven.plugins:maven-surefire-plugin:3.2.2` to `3.2.3`
* Added `org.apache.maven.plugins:maven-toolchains-plugin:3.1.0`
* Updated `org.codehaus.mojo:versions-maven-plugin:2.16.1` to `2.16.2`
