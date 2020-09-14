package com.exasol.containers.imagereference;

public interface DockerImageReference {

    /**
     * Get the Docker image reference for an Exasol version number.
     *
     * @return Docker image Reference
     */
    @Override
    public String toString();
}
