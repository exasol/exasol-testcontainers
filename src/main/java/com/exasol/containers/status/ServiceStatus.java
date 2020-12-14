package com.exasol.containers.status;

import java.io.Serializable;

/**
 * Status of a service (like BucketFs).
 */
public enum ServiceStatus implements Serializable {
    NOT_READY, // service is no available yet
    READY, // service can be used
    NOT_CHECKED // service state has not yet been determined
}