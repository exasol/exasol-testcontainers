package com.exasol.containers.status;

import java.io.Serializable;

/**
 * Status of a service (like BucketFs).
 */
public enum ServiceStatus implements Serializable {
    /** service is no available yet */
    NOT_READY,
    /** service can be used */
    READY,
    /** service state has not yet been determined */
    NOT_CHECKED
}