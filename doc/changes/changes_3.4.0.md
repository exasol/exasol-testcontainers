# Exasol Test Containers 3.4.0, released 2020-12-04

Code name: Matrix testing

## Summary

In the 3.4.0 release we added the ability to override the docker image used via the Java system property `com.exasol.dockerdb.image`. You can use this in a continuous integration build for example to iterate through a list of Exasol versions that you want to test your software against.

Shortened image references can now also be prefixed with `docker-db:` or `exasol/docker-db:`. Example: `docker-db:7`

You can now install JDBC drivers that you can use in the ExaLoader (for `IMPORT` statements) and User Defined Functions (UDFs) like this:

```java
final ExasolDriverManager driverManager = EXASOL.getDriverManager();
final DatabaseDriver driver = JdbcDriver.builder("EXAMPLE_DRIVER")
        .prefix("jdbc:examle:")
        .sourceFile(driverFile)
        .mainClass("org.example.Driver").build();
driverManager.install(driver); 
```

The release also now uses `project-keeper` to maintain uniform project setup and fixes some bugs and code smells.
 
## Features

* #102: `docker-db` now allowed as prefix for shortened docker image references.
* #106: Docker image can be overridden by property.
* #115: Added convenience method for installing drivers.

## Bugfixes

* #105: Adding "JDBC" as service in `withRequiredServices()` does not throw exception anymore.
* #108: Fixed code smells.

## Refactoring

* #106: Introduced CI matrix test with Exasol 6.1.12 and 7.0.4
* #112: Using `project-keeper` to manage common parts.

## Plugin Updates

* Added `com.exasol:project-keeper-maven-plugin:0.4.1`
* Updated `org.jacoco:jacoco-maven-plugin:0.8.5` to `0.8.6`
* Updated `org.codehaus.mojo:versions-maven-plugin:2.7` to `2.8.1`
* Updated `com.exasol:exasol-jdbc:7.0.3` to `7.0.4`
* Updated `org.mockito:mockito-junit-jupiter:3.6.0` to `3.6.28`
