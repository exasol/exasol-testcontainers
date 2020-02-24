package com.exasol.database;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.SQLException;

import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.exasol.containers.ExasolContainer;

@Testcontainers
class DatabaseServiceIT {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseServiceIT.class);
    @Container
    private static final ExasolContainer<? extends ExasolContainer<?>> CONTAINER = new ExasolContainer<>() //
            .withLogConsumer(new Slf4jLogConsumer(LOGGER)) //
            .withRequiredServices();
    private DatabaseService service;

    // [itest->dsn~database-service-stops-the-database~1]
    @BeforeEach
    void beforeEach() {
        this.service = CONTAINER.getDatabaseService("DB1");
    }

    // [itest->dsn~database-service-starts-the-database~1]
    @Order(1)
    @Test
    void testStop() throws InterruptedException {
        this.service.stop();
        assertThrows(SQLException.class, () -> CONTAINER.createConnectionForUser("SYS", "exasol"));
    }

    @Order(2)
    @Test
    void testRestart() throws InterruptedException {
        this.service.start();
        assertDoesNotThrow(() -> CONTAINER.createConnection(""));
    }
}