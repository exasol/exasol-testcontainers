package com.exasol.containers.imagereference;

/**
 * This classes models references to docker images, different from the official docker-db images.
 */
public class OtherDockerImageReference implements DockerImageReference {
    private final String reference;

    /**
     * Create a new instance of {@link OtherDockerImageReference}.
     * 
     * @param reference docker image reference string
     */
    OtherDockerImageReference(final String reference) {
        this.reference = reference;
    }

    @Override
    public String toString() {
        return this.reference;
    }
}
