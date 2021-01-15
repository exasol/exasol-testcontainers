# Exasol Test Containers 3.4.1, released 2021-01-15

Code name: Workaround for log rotation bug in docker-db

## Summary

In the 3.4.1 release we added a mitigation for a bug in [`docker-db`](https://github.com/exasol/docker-db) that causes log rotation to break after 40 minutes. Since ETC depends on reading the logs, this mitigation is necessary for running the container longer than 40 minutes.

## Bugfixes

* #118: Worked around log rotation bug in `docker-db`

## Plugin Updates

* Updated `com.exasol:project-keeper-maven-plugin:0.4.1` to `0.4.2`
