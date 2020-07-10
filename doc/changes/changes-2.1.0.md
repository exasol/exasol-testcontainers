# Exasol Test Containers 2.1.0, released 2020-07-10

ETC now supports downloading a file from BucketFS (e.g. in case you need a log from there).

We have also fixed a bug in the code that waits for an upload to complete that occurred when replacing an existing file. The call now properly blocks until the file is really replaced.

Creating the `ExasolContainer` is now more convenient. The constructor additionally accepts Exasol version numbers as parameters and picks the right Docker image automatically.

## API Changes

The constructors of both `UdfContainerWaitStrategy` and `BucketFsWaitStrategy` now have an additional UTC timestamp after which the services need to report being started.
Since this is an internal API that users neither need to nor should use, we did not increase the major version number.

The construct of the `LogPatternDetectorFactory` now requires an `ExasolContainer` as parameter instead of a `Container`. This is also an internal API and passing any other container type would not work anyway, since the log format is Exasol-specific.

## Features / Enhancements
 
* #54: Download a file from BucketFS.
* #54: Updated default docker image used by the `exasol-testcontainers` from `6.2.2-d1` to `6.2.7-d1`
* #56: Pick container image by Exasol version

## Bugfixes

* #54: Wait for file to be replaced in BucketFS.