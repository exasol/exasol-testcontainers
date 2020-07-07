# Exasol Test Containers 2.1.0, released 2020-07-03

In this release we added support for downloading a file from BucketFS (e.g. in case you need a log from there).

We also fixed a bug in the code that waits for an upload to complete that occurred when replacing an existing file. The call now properly blocks until the file is really replaced.

## API Changes

The constructors of both `UdfContainerWaitStrategy` and `BucketFsWaitStrategy` were changed to add a UTC timestamp after which the services need to report being started.
Since this is an internal API that users don't need (and shouldn't) use, we did not increase the major version number.

## Features / Enhancements
 
* #54: Download a file from BucketFS.

## Bugfixes

* #54: Wait for file to be replaced in BucketFS.