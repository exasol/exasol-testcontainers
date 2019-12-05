package com.exasol.containers;

import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.JdbcDatabaseContainerProvider;

/**
 * Factory for Exasol containers.
 */
public class ExasolContainerProvider extends JdbcDatabaseContainerProvider {
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(final String databaseType) {
        return databaseType.equals(ExasolContainerConstants.NAME);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JdbcDatabaseContainer<? extends JdbcDatabaseContainer<?>> newInstance(final String tag) {
        return new ExasolContainer<>(ExasolContainerConstants.EXASOL_DOCKER_IMAGE_ID + ":" + tag);
    }
}