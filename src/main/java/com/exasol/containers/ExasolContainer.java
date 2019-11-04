package com.exasol.containers;

import java.util.Set;

import org.testcontainers.containers.JdbcDatabaseContainer;

@SuppressWarnings("squid:S2160") // Superclass adds state but does not override equals() and hashCode().
public class ExasolContainer<T extends ExasolContainer<T>> extends JdbcDatabaseContainer<T> {
    public static final String NAME = "exasol";
    private static final int CONTAINER_INTERNAL_DATABASE_PORT = 8888;
    private static final int CONTAINER_INTERNAL_BUCKETFS_PORT = 6583;
    private static final String JDBC_DRIVER_CLASS = "com.exasol.jdbc.EXADriver";
    private String username = "SYS";

    // The following assignment intentionally contains the initial password for the database administrator.
    // Keep in mind that this project deals with disposable containers that should only be used in integration tests.
    @SuppressWarnings("squid:S2068")
    private String password = "EXASOL";

    public ExasolContainer(final String dockerImageName) {
        super(dockerImageName);
    }

    @Override
    protected void configure() {
        this.addExposedPorts(CONTAINER_INTERNAL_DATABASE_PORT, CONTAINER_INTERNAL_BUCKETFS_PORT);
        super.configure();
    }

    @Override
    public Set<Integer> getLivenessCheckPortNumbers() {
        return Set.of(getMappedPort(CONTAINER_INTERNAL_DATABASE_PORT));
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
    public T withUsername(final String username) {
        this.username = username;
        return self();
    }

    @Override
    public T withPassword(final String password) {
        this.password = password;
        return self();
    }
}