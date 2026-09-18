package com.exasol.containers;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.*;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("slow")
class LocalTcpServiceForwardingIT {
    // [itest->dsn~reverse-ssh-port-forwarding-to-test-host~1]
    @Test
    void testConnectsFromUdfToLocalTcpService() throws IOException, SQLException, InterruptedException, ExecutionException,
            TimeoutException {
        try (final ServerSocket serverSocket = new ServerSocket(0);
                final ExasolContainer<? extends ExasolContainer<?>> container = new ExasolContainer<>()) {
            container.start();
            final InetSocketAddress databaseAddress = container
                    .makeLocalTcpServiceAccessibleFromDatabase(serverSocket.getLocalPort());
            final ExecutorService executor = Executors.newSingleThreadExecutor();
            try {
                final Future<Socket> connection = executor.submit(serverSocket::accept);
                executeUdfConnection(container, databaseAddress);
                try (final Socket socket = connection.get(30, TimeUnit.SECONDS)) {
                    assertTrue(socket.isConnected());
                }
            } finally {
                executor.shutdownNow();
            }
        }
    }

    private void executeUdfConnection(final ExasolContainer<? extends ExasolContainer<?>> container,
            final InetSocketAddress databaseAddress) throws SQLException {
        try (final Connection connection = container.createConnection();
                final Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE SCHEMA TEST");
            statement.executeUpdate("CREATE OR REPLACE PYTHON3 SCALAR SCRIPT TEST.PING() RETURNS INT AS\n"
                    + "def run(ctx):\n"
                    + "  import socket\n"
                    + "  s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)\n"
                    + "  s.connect(('" + databaseAddress.getHostString() + "', " + databaseAddress.getPort() + "))\n"
                    + "  return 1\n/\n");
            try (final ResultSet resultSet = statement.executeQuery("SELECT TEST.PING()")) {
                assertTrue(resultSet.next());
            }
        }
    }
}
