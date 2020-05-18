package com.exasol.containers;

/**
 * Types of services an Exasol instance of cluster can provide.
 */
public enum ExasolService {
    /** User Defined Functions (UDFs) and the language containers */
    UDF,
    /** Bucket filesystem */
    BUCKETFS,
    /** database connection via JDBC */
    JDBC
}