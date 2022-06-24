# Test containers for Exasol on Docker 6.1.2, released 2022-06-22

Code name: Uniform Fingerprint URL

JDBC URLs now uniformly end in a semicolon, no matter if with or without fingerprint.

We replaced all uses of TestContainers now deprecated `getContainerID` method with `getHost`.

## Bugfixes

* #193: Unified `getJdbcUrlWithFingerprint()` and `getJdbcUrlWithoutFingerprint()`

## Dependency Updates

### Compile Dependency Updates

* Removed `org.apache.commons:commons-compress:1.21`
* Updated `org.testcontainers:jdbc:1.16.3` to `1.17.2`
* Updated `org.testcontainers:testcontainers:1.16.3` to `1.17.2`

### Runtime Dependency Updates

* Updated `com.exasol:exasol-jdbc:7.1.4` to `7.1.11`

### Test Dependency Updates

* Updated `org.mockito:mockito-junit-jupiter:4.3.1` to `4.6.1`
* Updated `org.testcontainers:junit-jupiter:1.16.3` to `1.17.2`

### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:0.7.1` to `1.1.1`
* Updated `com.exasol:project-keeper-maven-plugin:1.3.4` to `2.4.6`
* Updated `org.apache.maven.plugins:maven-compiler-plugin:3.10.0` to `3.10.1`
* Updated `org.apache.maven.plugins:maven-failsafe-plugin:3.0.0-M3` to `3.0.0-M5`
* Updated `org.apache.maven.plugins:maven-javadoc-plugin:3.3.2` to `3.4.0`
* Updated `org.apache.maven.plugins:maven-surefire-plugin:3.0.0-M3` to `3.0.0-M5`
* Added `org.codehaus.mojo:flatten-maven-plugin:1.2.7`
* Updated `org.codehaus.mojo:versions-maven-plugin:2.9.0` to `2.10.0`
* Updated `org.itsallcode:openfasttrace-maven-plugin:1.4.0` to `1.5.0`
* Updated `org.jacoco:jacoco-maven-plugin:0.8.7` to `0.8.8`
* Added `org.sonarsource.scanner.maven:sonar-maven-plugin:3.9.1.2184`
* Updated `org.sonatype.plugins:nexus-staging-maven-plugin:1.6.8` to `1.6.13`
