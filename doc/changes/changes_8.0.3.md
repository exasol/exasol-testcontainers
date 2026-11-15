# Test Containers for Exasol on Docker 8.0.3, released 2026-??-??

Code name: Fixed vulnerability CVE-2026-119078 in commons-codec:commons-codec:jar:1.19.0:compile

## Summary

This release fixes the following vulnerability:

### CVE-2026-119078 (CWE-1068) in dependency `commons-codec:commons-codec:jar:1.19.0:compile`
Apache Commons Codec's Blake3 class documented the output of its doFinalize methods as depending on any previously finalized bytes, so that consecutive calls would return different output. In fact, doFinalize does not change the hash state: calling it again without supplying more input returns the same bytes. The key-derivation example in the class Javadoc, shipped since 1.16.0, derives two keys with two consecutive doFinalize calls. Code that follows it gets identical keys, so the separation between the derived keys is lost. The same applies to any application that calls doFinalize more than once on one instance and expects new output each time.

This issue affects Apache Commons Codec from 1.16.0 before 1.23.0.

Version 1.23.0 corrects the documentation, but the behavior of doFinalize is unchanged. Upgrading alone does not fix affected code. Users should check their applications for repeated doFinalize calls on the same Blake3 instance and derive multiple keys by requesting the combined length in one call and splitting the output, as the updated Javadoc shows.
#### References
* https://guide.sonatype.com/vulnerability/CVE-2026-119078?component-type=maven&component-name=commons-codec%2Fcommons-codec&utm_source=ossindex-client&utm_medium=integration&utm_content=1.8.1
* http://web.nvd.nist.gov/view/vuln/detail?vulnId=CVE-2026-119078
* https://lists.apache.org/thread.html/ol71kdgopfxwlxgqmkd0jrfcw05wdq5z
* http://www.openwall.com/lists/oss-security/2026/11/09/29

## Security

* #301: Fixed vulnerability CVE-2026-119078 in dependency `commons-codec:commons-codec:jar:1.19.0:compile`

## Dependency Updates

### Compile Dependency Updates

* Updated `com.exasol:bucketfs-java:5.0.0` to `5.0.1`
* Updated `com.exasol:database-cleaner:2.0.0` to `2.0.1`
* Updated `com.github.mwiede:jsch:2.28.7` to `2.28.8`

### Runtime Dependency Updates

* Updated `com.exasol:exasol-jdbc:26.2.9` to `26.2.10`

### Test Dependency Updates

* Updated `com.exasol:hamcrest-resultset-matcher:1.7.3` to `1.7.4`
* Updated `com.exasol:udf-api-java:1.0.11` to `1.0.12`
* Updated `nl.jqno.equalsverifier:equalsverifier:3.19.4` to `4.5.2`
* Updated `org.junit.jupiter:junit-jupiter-params:5.14.4` to `6.1.3`
* Updated `org.mockito:mockito-junit-jupiter:5.23.0` to `5.24.0`

### Plugin Dependency Updates

* Updated `com.exasol:project-keeper-maven-plugin:5.7.5` to `5.8.2`
