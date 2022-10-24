# Test containers for Exasol on Docker 6.3.0, released 2022-10-24

Code name: SSH Access to Docker Container

## Summary

In the past Test Containers for Exasol used `docker exec` to connect to docker container in order to
* read the Exasol cluster configuration from file `/exa/etc/EXAConf`
* check the date and time used by the docker container
* verify contents of BucketFS
* verify synchronization of files in BucketFS by inspecting the BucketFS log files in `/exa/logs/cored/bucketfsd.*.log`

In future Exasol docker containers are planned to not support `docker exec` anymore but to require using SSH instead.

The current release therefore adds support for SSH connections to the docker container.

## Features

* #202: SSH access to docker container

## Dependency Updates

### Compile Dependency Updates

* Added `com.jcraft:jsch:0.1.55`

### Test Dependency Updates

* Added `org.junit-pioneer:junit-pioneer:1.7.1`

### Plugin Dependency Updates

* Updated `com.exasol:project-keeper-maven-plugin:2.6.2` to `2.8.0`
