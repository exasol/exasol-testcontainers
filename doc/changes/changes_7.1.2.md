# Test Containers for Exasol on Docker 7.1.2, released 2024-11-22

Code name: Allow withExposedPorts

## Summary

This release fixes an issue that prevented the container startup when using the option `withExposedPorts`.
The installation of ScriptLanguageContainers requires the BucketFS internal port to be available, but this shouldn't 
be the case when no SLC has been requested to be installed.

It also adapts the support of information retrieval at exit to work with Exasol v8.

The default docker image was bumped to 7.1.30.

## Bugfix

* #257: Container startup fails when using withExposedPorts()
* #254: Adapt SupportInformationRetriever for Exasol v8

## Dependency Updates

### Plugin Dependency Updates

* Updated `com.exasol:project-keeper-maven-plugin:4.3.3` to `4.4.0`
* Added `com.exasol:quality-summarizer-maven-plugin:0.2.0`
* Updated `io.github.zlika:reproducible-build-maven-plugin:0.16` to `0.17`
* Updated `org.apache.maven.plugins:maven-clean-plugin:2.5` to `3.4.0`
* Updated `org.apache.maven.plugins:maven-failsafe-plugin:3.2.5` to `3.5.1`
* Updated `org.apache.maven.plugins:maven-gpg-plugin:3.2.4` to `3.2.7`
* Updated `org.apache.maven.plugins:maven-install-plugin:2.4` to `3.1.3`
* Updated `org.apache.maven.plugins:maven-javadoc-plugin:3.7.0` to `3.10.1`
* Updated `org.apache.maven.plugins:maven-resources-plugin:2.6` to `3.3.1`
* Updated `org.apache.maven.plugins:maven-site-plugin:3.3` to `3.9.1`
* Updated `org.apache.maven.plugins:maven-surefire-plugin:3.2.5` to `3.5.1`
* Updated `org.codehaus.mojo:versions-maven-plugin:2.16.2` to `2.17.1`
