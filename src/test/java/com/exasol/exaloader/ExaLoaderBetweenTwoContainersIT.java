package com.exasol.exaloader;

import static com.exasol.containers.ExasolContainerAssumptions.assumeDockerDbVersionNotOverriddenToBelowExasolSevenOne;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.JdbcDatabaseContainer.NoDriverFoundException;
import org.testcontainers.containers.Network;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.exasol.containers.ExasolContainer;

@Tag("slow")
@Testcontainers
class ExaLoaderBetweenTwoContainersIT {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExaLoaderBetweenTwoContainersIT.class);

    @BeforeAll
    static void beforeAll() {
        // The tests below expect an HTTPS connection to be available, so we need at least version 7.1.
        assumeDockerDbVersionNotOverriddenToBelowExasolSevenOne();
    }

    // [itest->dsn~ip-address-in-common-docker-network~1]
    @Test
    void testImport() throws NoDriverFoundException, SQLException, UnsupportedOperationException, IOException,
            InterruptedException {
        try (final Network network = Network.newNetwork();
                final ExasolContainer<? extends ExasolContainer<?>> sourceContainer = createContainer();
                final ExasolContainer<? extends ExasolContainer<?>> targetContainer = createContainer() //
        ) {
            sourceContainer.withNetwork(network).withRequiredServices().start();
            targetContainer.withNetwork(network).withRequiredServices().start();
            final Connection sourceConnection = sourceContainer.createConnection("");
            executeStatements(sourceConnection, //
                    "CREATE SCHEMA SOURCE_SCHEMA", //
                    "CREATE TABLE SOURCE_SCHEMA.FRUITS(NAME VARCHAR(40))", //
                    "INSERT INTO SOURCE_SCHEMA.FRUITS VALUES ('apple'), ('banana'), ('cherry')");
            final Connection targetConnection = targetContainer.createConnection("");
            final String sourceConnectionUrl = sourceContainer.getDockerNetworkInternalIpAddress() + "/"
                    + sourceContainer.getTlsCertificateFingerprint().get() + ":"
                    + sourceContainer.getDefaultInternalDatabasePort();
            executeStatements(targetConnection, //
                    "CREATE CONNECTION SRCCON TO '" + sourceConnectionUrl + "' USER 'SYS' IDENTIFIED BY 'exasol'", //
                    "CREATE SCHEMA TARGET_SCHEMA", //
                    "CREATE TABLE TARGET_SCHEMA.FRUITS(NAME VARCHAR(40))", //
                    "IMPORT INTO TARGET_SCHEMA.FRUITS FROM EXA AT SRCCON TABLE SOURCE_SCHEMA.FRUITS");
            final Statement assertionStatement = targetConnection.createStatement();
            final ResultSet result = assertionStatement.executeQuery("SELECT * FROM TARGET_SCHEMA.FRUITS");
            final List<String> fruits = new ArrayList<>();
            while (result.next()) {
                fruits.add(result.getString("NAME"));
            }
            assertThat(fruits, containsInAnyOrder("apple", "banana", "cherry"));
        }
    }

    private ExasolContainer<? extends ExasolContainer<?>> createContainer() {
        return new ExasolContainer<>();
    }

    private void executeStatements(final Connection connection, final String... sqls) throws SQLException {
        final Statement sourceStatement = connection.createStatement();
        for (final String sql : sqls) {
            LOGGER.info("Executing statement:" + sql);
            sourceStatement.execute(sql);
        }
    }
}