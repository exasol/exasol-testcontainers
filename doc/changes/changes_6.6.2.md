# Test Containers for Exasol on Docker 6.6.2, released 2023-09-25

Code name: Fix vulnerability CVE-2023-42503

## Summary

This release fixes vulnerability CVE-2023-42503 in transitive dependency `org.apache.commons:commons-compress`.

The release also updates the default Exasol version to 7.1.23.

## Security

* #235: Upgraded dependencies

## Dependency Updates

### Compile Dependency Updates

* Added `org.apache.commons:commons-compress:1.24.0`
* Updated `org.testcontainers:jdbc:1.18.3` to `1.19.0`
* Updated `org.testcontainers:testcontainers:1.18.3` to `1.19.0`

### Test Dependency Updates

* Updated `org.junit-pioneer:junit-pioneer:2.0.1` to `2.1.0`
* Updated `org.junit.jupiter:junit-jupiter-engine:5.9.3` to `5.10.0`
* Updated `org.junit.jupiter:junit-jupiter-params:5.9.3` to `5.10.0`
* Updated `org.mockito:mockito-junit-jupiter:5.4.0` to `5.5.0`
* Updated `org.testcontainers:junit-jupiter:1.18.3` to `1.19.0`

### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:1.2.3` to `1.3.0`
* Updated `com.exasol:project-keeper-maven-plugin:2.9.7` to `2.9.11`
* Updated `org.apache.maven.plugins:maven-enforcer-plugin:3.3.0` to `3.4.0`
* Updated `org.apache.maven.plugins:maven-failsafe-plugin:3.0.0` to `3.1.2`
* Updated `org.apache.maven.plugins:maven-gpg-plugin:3.0.1` to `3.1.0`
* Updated `org.apache.maven.plugins:maven-surefire-plugin:3.0.0` to `3.1.2`
* Updated `org.codehaus.mojo:flatten-maven-plugin:1.4.1` to `1.5.0`
* Updated `org.codehaus.mojo:versions-maven-plugin:2.15.0` to `2.16.0`
* Updated `org.jacoco:jacoco-maven-plugin:0.8.9` to `0.8.10`
