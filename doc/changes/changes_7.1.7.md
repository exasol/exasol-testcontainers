# Test Containers for Exasol on Docker 7.1.7, released 2025-07-17

Code name: Security update on top of 7.1.6

## Summary

This is a security update release. We updated all direct dependencies to their respective latest versions.

We also updated the following transitive dependencies:

`org.apache.commons:commons-compress` from 1.12.4 (coming from testcontainers) to 1.26.1 to fix CVE-2024-25710 and CVE-2024-26308. These vulnerabilities could allow attackers to cause denial of service through infinite loops when processing corrupted DUMP files [[1]](https://www.cvedetails.com/cve/CVE-2024-25710/) or trigger out-of-memory errors via resource exhaustion [[2]](https://www.cvedetails.com/cve/CVE-2024-26308/).

`org.apache.commons:commons-lang3` from 3.14.0 to 3.18.0 to fix CVE-2025-48924. This vulnerability could lead to application crashes through uncontrolled recursion when processing certain inputs, potentially causing StackOverflowError [[3]](https://nvd.nist.gov/vuln/detail/CVE-2025-48924).

## Security

* 274: Fixed CVE-2024-25710, CVE-2024-26308 and CVE-2025-48924 by updating transitive dependencies. 

## Dependency Updates

### Compile Dependency Updates

* Updated `com.exasol:database-cleaner:1.1.3` to `1.1.4`
* Updated `com.github.mwiede:jsch:0.2.24` to `2.27.2`
* Added `org.apache.commons:commons-compress:1.26.1`
* Added `org.apache.commons:commons-lang3:3.18.0`
* Updated `org.testcontainers:jdbc:1.20.6` to `1.21.3`
* Updated `org.testcontainers:testcontainers:1.20.6` to `1.21.3`

### Runtime Dependency Updates

* Updated `com.exasol:exasol-jdbc:25.2.3` to `25.2.4`
* Removed `commons-io:commons-io:2.18.0`
* Removed `org.apache.commons:commons-compress:1.27.1`

### Test Dependency Updates

* Updated `com.exasol:hamcrest-resultset-matcher:1.7.0` to `1.7.1`
* Updated `com.exasol:udf-api-java:1.0.5` to `1.0.6`
* Updated `nl.jqno.equalsverifier:equalsverifier:3.19.1` to `3.19.4`
* Updated `org.junit.jupiter:junit-jupiter-api:5.12.0` to `5.13.3`
* Updated `org.junit.jupiter:junit-jupiter-params:5.12.0` to `5.13.3`
* Updated `org.mockito:mockito-junit-jupiter:5.16.0` to `5.18.0`
* Updated `org.testcontainers:junit-jupiter:1.20.6` to `1.21.3`

### Plugin Dependency Updates

* Updated `com.exasol:project-keeper-maven-plugin:5.1.0` to `5.2.2`
* Added `org.sonatype.central:central-publishing-maven-plugin:0.7.0`
* Removed `org.sonatype.plugins:nexus-staging-maven-plugin:1.7.0`
