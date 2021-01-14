package com.exasol.containers.workarounds;

/**
 * Common interface for all Exasol test container workarounds.
 */
public interface Workaround {
    /**
     * Get the name of the workaround.
     *
     * @return name of the workaround
     */
    public String getName();

    /**
     * Check whether the workaround is necessary in the current environment.
     * <p>
     * Each workaround defines its own criteria for when it needs to be applied, depending on the environment like
     * Exasol version.
     * </p>
     *
     * @return {@code true} if the workaround is necessary
     */
    public boolean isNecessary();

    /**
     * Apply the workaround.
     *
     * @throws WorkaroundException if the workaround could not be applied
     */
    public void apply() throws WorkaroundException;
}