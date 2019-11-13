package com.exasol.bucketfs;

import java.net.URI;

/**
 * Exception for problems when accessing buckets or their contents in Exasol's BucketFS.
 */
public class BucketAccessException extends Exception {
    private static final long serialVersionUID = -1002852289020779835L;
    private final URI uri;
    private final int statusCode;

    /**
     * Create a new instance of a {@link BucketAccessException}.
     *
     * @param message error message
     * @param uri     URI that was attempted to access
     * @param cause   exception that caused this one
     */
    public BucketAccessException(final String message, final URI uri, final Throwable cause) {
        super(message + " URI: " + uri, cause);
        this.uri = uri;
        this.statusCode = 0;
    }

    /**
     * Create a new instance of a {@link BucketAccessException}.
     *
     * @param message    error message
     * @param statusCode HTTP response code
     * @param uri        URI that was attempted to access
     */
    public BucketAccessException(final String message, final int statusCode, final URI uri) {
        super(message + "URI: " + uri + " (Status " + statusCode + ")");
        this.statusCode = statusCode;
        this.uri = uri;
    }

    /**
     * Create a new instance of a {@link BucketAccessException}
     *
     * @param message error message
     * @param cause   exception that caused this one
     */
    public BucketAccessException(final String message, final Throwable cause) {
        super(message, cause);
        this.uri = null;
        this.statusCode = 0;
    }

    /**
     * @return URI that was tried to access
     */
    public URI getUri() {
        return this.uri;
    }

    /**
     * @return HTTP status code
     */
    public int getStatusCode() {
        return this.statusCode;
    }
}