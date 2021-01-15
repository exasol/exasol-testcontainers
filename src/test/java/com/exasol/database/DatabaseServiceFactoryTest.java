package com.exasol.database;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exasol.config.ClusterConfiguration;

@Tag("fast")
@ExtendWith(MockitoExtension.class)
class DatabaseServiceFactoryTest {
    @Mock
    private ClusterConfiguration configurationMock;

    @Test
    void testGetDatabaseService() {
        final String databaseName = "foo";
        when(this.configurationMock.containsDatabaseService(databaseName)).thenReturn(true);
        final DatabaseServiceFactory factory = new DatabaseServiceFactory(null, this.configurationMock);
        assertThat(factory.getDatabaseService(databaseName).getDatabaseName(), equalTo(databaseName));
    }

    @Test
    void testGetDatabaseServiceThrowsExceptionIfDatabaseNameDoesNotExist() {
        when(this.configurationMock.containsDatabaseService(anyString())).thenReturn(false);
        final DatabaseServiceFactory factory = new DatabaseServiceFactory(null, this.configurationMock);
        assertThrows(IllegalArgumentException.class, () -> factory.getDatabaseService("nonexistent"));
    }
}