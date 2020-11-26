# Exasol Test Containers 3.4.0, released 2020-11-26

Code name: Matrix testing

In the 3.4.0 release we added the ability to override the docker image used via the Java system property `com.exasol.dockerdb.image`. You can use this in a continuous integration build for example to iterate through a list of Exasol versions that you want to test your software against.
 
## Features
 
* #106: Docker image can be overridden by property.