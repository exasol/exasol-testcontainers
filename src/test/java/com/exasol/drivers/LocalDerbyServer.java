package com.exasol.drivers;

import java.io.PrintWriter;
import java.net.InetAddress;
import java.nio.file.Path;
import java.sql.*;

import org.apache.derby.drda.NetworkServerControl;

class LocalDerbyServer implements AutoCloseable {
    public static final String DRIVER_NAME = "DERBY";
    public static final int DERBY_PORT = 1527;
    public static final String DERBY_USER = "app";
    public static final String DERBY_PASSWORD = "open_sesame";

    private final NetworkServerControl derbyServer;
    private final String hostIP;

    private LocalDerbyServer(final NetworkServerControl derbyServer, final String hostIP) {
        this.derbyServer = derbyServer;
        this.hostIP = hostIP;
    }

    static LocalDerbyServer start(final Path derbyHomeDir, final String hostIP) {
        final NetworkServerControl derbyServer = startDerbyServer(derbyHomeDir, hostIP);
        waitForDerbyServerToAcceptConnections(derbyServer);
        return new LocalDerbyServer(derbyServer, hostIP);
    }

    private static NetworkServerControl startDerbyServer(final Path derbyHomeDir, final String hostIP) {
        System.setProperty("derby.system.home", derbyHomeDir.toString());
        System.setProperty("derby.user." + DERBY_USER, DERBY_PASSWORD);
        try {
            final NetworkServerControl derbyServer = new NetworkServerControl(InetAddress.getByName(hostIP),
                    DERBY_PORT);
            derbyServer.start(new PrintWriter(System.out));
            return derbyServer;
        } catch (final Exception exception) {
            throw new IllegalStateException("Failed to start derby server", exception);
        }
    }

    @SuppressWarnings("java:S2925") // We need to wait for the server to come up with "sleep" in a loop.
    private static void waitForDerbyServerToAcceptConnections(final NetworkServerControl derbyServer) {
        for (int attempts = 0; attempts < 20; ++attempts) {
            try {
                derbyServer.ping();
                return;
            } catch (final Exception exception) {
                sleep();
            }
        }
        throw new IllegalStateException("Derby database server required for integration tests did not start up.");
    }

    private static void sleep() {
        try {
            Thread.sleep(500);
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Failed to wait for derby server");
        }
    }

    String getDriverName() {
        return DRIVER_NAME;
    }

    String getUsername() {
        return DERBY_USER;
    }

    String getPassword() {
        return DERBY_PASSWORD;
    }

    Connection getConnection() {
        final String jdbcUrl = "jdbc:derby://" + hostIP + ":" + DERBY_PORT + "/test;create=true;";
        try {
            return DriverManager.getConnection(jdbcUrl);
        } catch (final SQLException exception) {
            throw new IllegalStateException("Failed to connect to '" + jdbcUrl + "': " + exception.getMessage(),
                    exception);
        }
    }

    Driver getDriver() {
        try {
            return DriverManager.getDriver("jdbc:derby://localhost");
        } catch (final SQLException exception) {
            throw new IllegalStateException("Failed to get derby driver: " + exception.getMessage(), exception);
        }
    }

    @Override
    public void close() {
        shutDownDerbyServer();
    }

    private void shutDownDerbyServer() {
        if (derbyServer != null) {
            try {
                DriverManager.getConnection("jdbc:derby://" + hostIP + "/test;shutdown=true", getUsername(),
                        getPassword());
            } catch (final SQLNonTransientConnectionException exception) {
                if (!exception.getMessage().contains("SQLSTATE: 08006")) {
                    throw new RuntimeException("Failed to shut down Derby database server.", exception);
                }
            } catch (final SQLException exception) {
                throw new RuntimeException("Failed to shut down Derby database server.", exception);
            }
        }
    }
}
