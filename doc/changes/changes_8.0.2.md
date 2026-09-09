# Test Containers for Exasol on Docker 8.0.2, released 2026-??-??

Code name: Fixed vulnerability CVE-2026-86231 in com.github.mwiede:jsch:jar:2.28.5:compile

## Summary

This release fixes the following vulnerability:

### CVE-2026-86231 (CWE-298) in dependency `com.github.mwiede:jsch:jar:2.28.5:compile`
A security flaw has been discovered in mwiede jsch up to 2.28.5. Affected is the function getRevokedKeys of the file src/main/java/com/jcraft/jsch/KnownHosts.java. Performing a manipulation of the argument known_hosts results in improper check for certificate revocation. The attack is possible to be carried out remotely. The attack is considered to have high complexity. The exploitability is told to be difficult. The exploit has been released to the public and may be used for attacks. Upgrading to version 2.28.6 is able to address this issue. The patch is named 194a2f76a5c0f1c3f778565be3fd66bcafc42d23. You should upgrade the affected component.
#### References
* https://guide.sonatype.com/vulnerability/CVE-2026-86231?component-type=maven&component-name=com.github.mwiede%2Fjsch&utm_source=ossindex-client&utm_medium=integration&utm_content=1.8.1
* http://web.nvd.nist.gov/view/vuln/detail?vulnId=CVE-2026-86231
* https://github.com/mwiede/jsch/issues/1091
* https://github.com/mwiede/jsch/pull/1098
* https://github.com/mwiede/jsch/releases/tag/jsch-2.28.6

## Security

* #295: Fixed vulnerability CVE-2026-86231 in dependency `com.github.mwiede:jsch:jar:2.28.5:compile`

## Dependency Updates

### Compile Dependency Updates

* Updated `com.exasol:database-cleaner:1.1.6` to `2.0.0`
* Updated `com.github.mwiede:jsch:2.28.5` to `2.28.7`

### Runtime Dependency Updates

* Updated `com.exasol:exasol-jdbc:26.2.8` to `26.2.9`

### Test Dependency Updates

* Updated `com.exasol:hamcrest-resultset-matcher:1.7.2` to `1.7.3`
* Updated `com.exasol:udf-api-java:1.0.10` to `1.0.11`
* Updated `nl.jqno.equalsverifier:equalsverifier:3.19.4` to `4.5.2`
* Updated `org.junit.jupiter:junit-jupiter-params:5.14.4` to `6.1.3`

### Plugin Dependency Updates

* Updated `com.exasol:project-keeper-maven-plugin:5.7.4` to `5.7.5`
