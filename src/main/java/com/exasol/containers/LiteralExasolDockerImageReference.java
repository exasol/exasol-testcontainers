package com.exasol.containers;

import com.exasol.errorreporting.ExaError;

/**
 * Docker image reference for images that don't follow the established standard of naming Exasol Docker images.
 */
class LiteralExasolDockerImageReference implements ExasolDockerImageReference {
    private static final String ILLEGAL_VERSION_DETAIL_ACCESS_MESSAGE = ExaError.messageBuilder("F-ETC-4")
            .message("Can't get version details from a non-standard Exasol image reference." //
                    + " Adhere to the naming conventions for docker-db images to use this function.") //
            .toString();
    private final String reference;

    /**
     * Create a new docker image reference from a literal ID.
     *
     * @param reference literal ID
     */
    public LiteralExasolDockerImageReference(final String reference) {
        this.reference = reference;
    }

    @Override
    public int getMajor() {
        throw new IllegalStateException(ILLEGAL_VERSION_DETAIL_ACCESS_MESSAGE);
    }

    @Override
    public boolean hasMajor() {
        return false;
    }

    @Override
    public int getMinor() {
        throw new IllegalStateException(ILLEGAL_VERSION_DETAIL_ACCESS_MESSAGE);
    }

    @Override
    public boolean hasMinor() {
        return false;
    }

    @Override
    public int getFixVersion() {
        throw new IllegalStateException(ILLEGAL_VERSION_DETAIL_ACCESS_MESSAGE);
    }

    @Override
    public boolean hasFix() {
        return false;
    }

    @Override
    public int getDockerImageRevision() {
        throw new IllegalStateException(ILLEGAL_VERSION_DETAIL_ACCESS_MESSAGE);
    }

    @Override
    public boolean hasDockerImageRevision() {
        return false;
    }

    @Override
    public String toString() {
        return this.reference;
    }

    @Override
    public String getPrefix() {
        throw new IllegalStateException(ILLEGAL_VERSION_DETAIL_ACCESS_MESSAGE);
    }

    @Override
    public boolean hasPrefix() {
        return false;
    }
    @Override
    public String getSuffix() {
        throw new IllegalStateException(ILLEGAL_VERSION_DETAIL_ACCESS_MESSAGE);
    }

    @Override
    public boolean hasSuffix() {
        return false;
    }
}