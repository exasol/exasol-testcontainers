package com.exasol.database;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.*;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.exasol.containers.ExasolContainer;
import com.exasol.containers.UncheckedSqlException;

@Tag("slow")
@Testcontainers
class DatabaseServiceIT {
    @Container
    private static final ExasolContainer<? extends ExasolContainer<?>> CONTAINER = new ExasolContainer<>()
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
        final UncheckedSqlException exception = assertThrows(UncheckedSqlException.class,
                () -> CONTAINER.createConnectionForUser("SYS", "exasol"));
        assertThat(exception.getMessage(), startsWith("E-ETC-26: Failed to connect to 'jdbc:exa:"));
    }

    @Order(2)
    @Test
    void testRestart() throws InterruptedException {
        this.service.start();
        assertDoesNotThrow(() -> CONTAINER.createConnection(""));
    }
}
