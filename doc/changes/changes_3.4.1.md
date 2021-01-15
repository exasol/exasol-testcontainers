# Exasol Test Containers 3.4.1, released 2021-01-15

Code name: Workaround for log rotation bug in docker-db

## Summary

In the 3.4.1 release we added a mitigation for a bug in [`docker-db`](https://github.com/exasol/docker-db) that causes log rotation to break after 40 minutes. Since ETC depends on reading the logs, this mitigation is necessary for running the container longer than 40 minutes.

Since this change required longer running integration tests, we added three build profiles to the Maven configuration:

* fast
* slow
* expensive

"Fast" means all test that don't take longer than a second or two. "Slow" test can run for a handful of minutes and "expensive" tests run longer than 10 minutes.

The default is now "fast" for regular builds and "slow" for Travis CI builds.

You can pick your profile by starting the Maven command with `-P`:

```bash
mvn -P expensive ...
```

## Bugfixes

* #118: Worked around log rotation bug in `docker-db`
* #120: Option to run only fast tests

## Plugin Updates

* Updated `com.exasol:project-keeper-maven-plugin:0.4.1` to `0.4.2`