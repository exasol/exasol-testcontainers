package com.exasol.containers;

import org.testcontainers.containers.JdbcDatabaseContainer;

public class ExasolContainer<SELF extends ExasolContainer<SELF>> extends JdbcDatabaseContainer<SELF> {
    public static final String NAME = "exasol";
    private static final int CONTAINER_INTERNAL_DATABASE_PORT = 8888;
    private static final int CONTAINER_INTERNAL_BUCKETFS_PORT = 6583;
    private static final String JDBC_DRIVER_CLASS = "com.exasol.jdbc.EXADriver";
    private static final String DEFAULT_USER = "SYS";
    private static final String INITIAL_PWD = "EXASOL";
    private String username = DEFAULT_USER;
    private String password = INITIAL_PWD;

    public ExasolContainer(final String dockerImageName) {
        super(dockerImageName);
    }

    @Override
    protected void configure() {
        this.addExposedPorts(CONTAINER_INTERNAL_DATABASE_PORT, CONTAINER_INTERNAL_BUCKETFS_PORT);
        super.configure();
    }

    @Override
    protected Integer getLivenessCheckPort() {
        return getMappedPort(CONTAINER_INTERNAL_DATABASE_PORT);
    }

    @Override
    public String getDriverClassName() {
        return JDBC_DRIVER_CLASS;
    }

    @Override
    public String getJdbcUrl() {
        return "jdbc:exa:" + getContainerIpAddress() + ":" + getMappedPort(CONTAINER_INTERNAL_DATABASE_PORT);
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    protected String getTestQueryString() {
        return "SELECT 1 FROM DUAL";
    }

    @Override
    public SELF withUsername(final String username) {
        this.username = username;
        return self();
    }

    @Override
    public SELF withPassword(final String password) {
        this.password = password;
        return self();
    }
}