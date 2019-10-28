package com.exasol.containers;

import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.JdbcDatabaseContainerProvider;

public class ExasolContainerProvider extends JdbcDatabaseContainerProvider {

    @Override
    public boolean supports(final String databaseType) {
        return databaseType.equals(ExasolContainer.NAME);
    }

    @Override
    public JdbcDatabaseContainer newInstance(final String tag) {
        return new ExasolContainer<>(ExasolContainer.IMAGE_ID + ":" + tag);
    }
}