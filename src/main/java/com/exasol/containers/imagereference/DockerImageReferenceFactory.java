package com.exasol.containers.imagereference;

/**
 * Factory for {@link DockerImageReference}.
 */
public class DockerImageReferenceFactory {
    private static final DockerImageReferenceFactory INSTANCE = new DockerImageReferenceFactory();

    private DockerImageReferenceFactory() {
        // empty on purpose
    }

    /**
     * Get a singleton instance of {@link DockerImageReferenceFactory}.
     * 
     * @return singleton instance of {@link DockerImageReferenceFactory}
     */
    public static DockerImageReferenceFactory getInstance() {
        return INSTANCE;
    }

    /**
     * Create a new instance of an {@link ExasolDockerImageReference} by parsing a reference string.
     * <p>
     * The following shortened reference strings are are supported and reference the standard Exasol {@code docker-db}:
     * </p>
     * <ul>
     * <li>&lt;major&gt; (7)</li>
     * <li>&lt;major&gt;.&lt;minor&gt; (7.1)</li>
     * <li>&lt;major&gt;.&lt;minor&gt;.&lt;fix&gt; (7.1.5)</li>
     * <li>&lt;major&gt;.&lt;minor&gt;.&lt;fix&gt;-&lt;docker-image-revision&gt; (7.1.5-d2)</li>
     * </ul>
     * <p>
     * Anything else is treated like a regular Docker image reference string.
     * </p>
     *
     * @param reference docker image reference or Exasol version number
     * @return reference to a {@code docker-db} image containing Exasol
     */
    public DockerImageReference parse(final String reference) {
        try {
            return new ExasolDockerImageReference(reference);
        } catch (final ExasolDockerImageReference.NotAnExasolImageReferenceException exception) {
            return new OtherDockerImageReference(reference);
        }
    }
}
