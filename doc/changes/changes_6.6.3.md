# Test Containers for Exasol on Docker 6.6.3, released 2023-11-08

Code name: Fixed CVE-2023-4043 in `org.eclipse.parsson:parsson`

## Summary

This release fixes CVE-2023-4043 in runtime dependency `org.eclipse.parsson:parsson`.

The release also updates the default Exasol version to 7.1.24.

**Known Issue:** When configuring a cluster log path by calling `ExasolContainer.withClusterLogsPath()` startup of the container fails with a timeout when waiting for the JDBC connection. This will be fixed in [#240](https://github.com/exasol/exasol-testcontainers/issues/240).

## Security

* #239: Fixed CVE-2023-4043 in `org.eclipse.parsson:parsson`

## Dependency Updates

### Compile Dependency Updates

* Updated `com.exasol:bucketfs-java:3.1.0` to `3.1.1`
* Updated `com.exasol:database-cleaner:1.1.0` to `1.1.1`
* Updated `org.testcontainers:jdbc:1.19.0` to `1.19.1`
* Updated `org.testcontainers:testcontainers:1.19.0` to `1.19.1`

### Test Dependency Updates

* Updated `com.exasol:udf-api-java:1.0.2` to `1.0.3`
* Updated `org.junit.jupiter:junit-jupiter-engine:5.10.0` to `5.10.1`
* Updated `org.junit.jupiter:junit-jupiter-params:5.10.0` to `5.10.1`
* Updated `org.mockito:mockito-junit-jupiter:5.5.0` to `5.7.0`
* Updated `org.testcontainers:junit-jupiter:1.19.0` to `1.19.1`

### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:1.3.0` to `1.3.1`
* Updated `com.exasol:project-keeper-maven-plugin:2.9.11` to `2.9.15`
* Updated `org.apache.maven.plugins:maven-enforcer-plugin:3.4.0` to `3.4.1`
* Updated `org.apache.maven.plugins:maven-failsafe-plugin:3.1.2` to `3.2.2`
* Updated `org.apache.maven.plugins:maven-javadoc-plugin:3.5.0` to `3.6.0`
* Updated `org.codehaus.mojo:versions-maven-plugin:2.16.0` to `2.16.1`
* Updated `org.jacoco:jacoco-maven-plugin:0.8.10` to `0.8.11`
* Updated `org.sonarsource.scanner.maven:sonar-maven-plugin:3.9.1.2184` to `3.10.0.2594`
