package com.exasol.containers;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.sql.SQLException;
import java.sql.Statement;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

// [utest->dsn~purging~1]
@Testcontainers
class ExasolPurgerIT {

    @Container // [itest->dsn~exasol-container-starts-with-test~1]
    private static final ExasolContainer<? extends ExasolContainer<?>> CONTAINER = new ExasolContainer<>()
            .withReuse(true);
    private static Statement STATEMENT;
    private static ExasolPurger PURGER;

    @BeforeAll
    static void beforeAll() throws SQLException {
        STATEMENT = CONTAINER.createConnectionForUser(CONTAINER.getUsername(), CONTAINER.getPassword())
                .createStatement();
        PURGER = new ExasolPurger(STATEMENT);
    }

    @AfterEach
    void after() throws SQLException {
        PURGER.purge();
    }

    @Test
    void testPurgeSchema() throws SQLException {
        createSchema();
        PURGER.purge();
        assertDoesNotThrow(this::createSchema);
    }

    @Test
    void testPurgeTable() throws SQLException {
        createSchema();
        createTable();
        PURGER.purge();
        createSchema();
        assertDoesNotThrow(this::createTable);
    }

    @Test
    void testPurgeConnection() throws SQLException {
        createConnection();
        PURGER.purge();
        assertDoesNotThrow(this::createConnection);
    }

    @Test
    void testPurgeUser() throws SQLException {
        createUser();
        PURGER.purge();
        assertDoesNotThrow(this::createUser);
    }

    @Test
    void testPurgeRole() throws SQLException {
        createRole();
        PURGER.purge();
        assertDoesNotThrow(this::createRole);
    }

    @Test
    void testPurgeFunction() throws SQLException {
        createFunction();
        PURGER.purge();
        assertDoesNotThrow(this::createFunction);
    }

    private void createFunction() throws SQLException {
        createSchema();
        STATEMENT.executeUpdate("CREATE FUNCTION MY_FUNCTION () RETURN VARCHAR(10)\n BEGIN\n RETURN 'test';\n END\n /");
    }

    private void createRole() throws SQLException {
        STATEMENT.executeUpdate("CREATE ROLE test_role;");
    }

    private void createUser() throws SQLException {
        STATEMENT.executeUpdate("CREATE USER user_1 IDENTIFIED BY \"h12_xhz\"");
    }

    private void createSchema() throws SQLException {
        STATEMENT.executeUpdate("CREATE SCHEMA TEST;");
    }

    private void createTable() throws SQLException {
        STATEMENT.executeUpdate("CREATE TABLE TEST_SCHEMA.TEST_TABLE (ID VARCHAR(10) UTF8);");
    }

    private void createConnection() throws SQLException {
        STATEMENT.executeUpdate("CREATE CONNECTION exa_connection TO '192.168.6.11:8563';");
    }
}