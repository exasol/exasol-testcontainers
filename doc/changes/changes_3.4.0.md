# Exasol Test Containers 3.4.0, released 2020-12-01

Code name: Matrix testing

In the 3.4.0 release we added the ability to override the docker image used via the Java system property `com.exasol.dockerdb.image`. You can use this in a continuous integration build for example to iterate through a list of Exasol versions that you want to test your software against.

The release also now uses `project-keeper` to maintain uniform project setup and fixes some bugs and code smells.
 
## Features
 
* #106: Docker image can be overridden by property.

## Bugfixes

* #105: Fixed code smells.
* #109: Adding "JDBC" as service in `withRequiredServices()` does not throw exception anymore.

## Refactoring

* #106: Introduced CI matrix test with Exasol 6.1.12 and 7.0.4
* #112: Using `project-keeper` to manage common parts.

## Plugin Updates

* Added `com.exaso.l:project-keeper-maven-plugin:0.4.1`
* Updated `org.jacoco:jacoco-maven-plugin:0.8.5` to `0.8.6`
* Updated `org.codehaus.mojo:versions-maven-plugin:2.7` to `2.8.1`
* Updated `com.exasol:exasol-jdbc:7.0.3` to `7.0.4`
* Updated `org.mockito:mockito-junit-jupiter:3.6.0` to `3.6.28`