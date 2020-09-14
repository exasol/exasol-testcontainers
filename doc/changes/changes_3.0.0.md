# Exasol Test Containers 3.0.0, released 2020-09-14

Code name: Container reuse and automatic port detection

Starting with this release the port numbers used for the test containers are read from the Exasol cluster configuration at runtime.

This allows you to use Docker images that have the database service(s) or BucketFS service(s) running on non-standard ports.

Keep in mind that you still need to expose the right port numbers at construction time using `withExposedPorts(...)`. The reason for this is that the cluster configuration is readable only after the container started and the container won't start if the database port is not exposed, since that prevents an alive-check.

We had to make one not-backward compatible interface change. `getDatabaseNames()` on the `ClusterConfiguration` now returns a list instead of a set. This was necessary, because we need to know which the first listed database is, since this is always the one used for the initial start-up check.

## Features / Enhancements
 
* #70: Improved exposed port handling
* #72: Added support for reusing containers

## Dependency updates

* Updated `org.itsallcode:openfasttrace-maven-plugin` from 0.1.0 to 1.0.0
* Updated `com.exasol:exasol-jdbc` from 6.2.3 to 6.2.5
* Updated `org.mockito:mockito-junit-jupiter` from 3.3.3 to 3.4.4
