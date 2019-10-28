package com.exasol.containers;

import org.testcontainers.containers.JdbcDatabaseContainer;

public class ExasolContainer<SELF extends ExasolContainer<SELF>> extends JdbcDatabaseContainer<SELF> {
    public static final Object NAME = "exasol";
    public static final String IMAGE_ID = "exasol/docker-db";
    private static final int CONTAINER_INTERNAL_DATABASE_PORT = 8888;
    private static final int CONTAINER_INTERNAL_BUCKETFS_PORT = 6583;

    public ExasolContainer(final String dockerImageName) {
        super(dockerImageName);
        this.addExposedPorts(CONTAINER_INTERNAL_DATABASE_PORT, CONTAINER_INTERNAL_BUCKETFS_PORT);
    }

    @Override
    public String getDriverClassName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getJdbcUrl() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getUsername() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getPassword() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected String getTestQueryString() {
        // TODO Auto-generated method stub
        return null;
    }
}