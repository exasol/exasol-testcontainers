package com.exasol.containers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.containers.ContainerLaunchException;
import org.testcontainers.containers.JdbcDatabaseContainer.NoDriverFoundException;

@ExtendWith(MockitoExtension.class)
class ExasolContainerTest {
    @Mock
    private Connection connectionMock;
    private ExasolContainer<? extends ExasolContainer<?>> containerSpy;

    @BeforeEach
    void beforeEach() throws NoDriverFoundException, SQLException {
        final ExasolContainer<?> container = new ExasolContainer<>();
        container.withRequiredServices();
        this.containerSpy = spy(container);
    }

    @Test
    void testWaitUntilContainerStarted(@Mock final Statement statementMock, @Mock final ResultSet resultSetMock)
            throws Exception {
        doNothing().when(this.containerSpy).waitUntilClusterConfigurationAvailable();
        doReturn(this.connectionMock).when(this.containerSpy).createConnection(any());
        when(this.connectionMock.createStatement()).thenReturn(statementMock);
        when(statementMock.executeQuery(anyString())).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(true);
        assertDoesNotThrow(() -> this.containerSpy.waitUntilContainerStarted());
    }

    @Test
    void testWaitUntilContainerStartedTimesOut() throws Exception {
        doNothing().when(this.containerSpy).waitUntilClusterConfigurationAvailable();
        doReturn(this.connectionMock).when(this.containerSpy).createConnection(any());
        when(this.connectionMock.createStatement()).thenThrow(new SQLException("Mock Exception"));
        assertThrowsLaunchException("timed out", () -> this.containerSpy.waitUntilContainerStarted());
    }

    private void assertThrowsLaunchException(final String expecetedMessageFragement, final Executable executable) {
        final ContainerLaunchException exception = assertThrows(ContainerLaunchException.class, executable);
        assertThat(exception.getMessage(), containsString(expecetedMessageFragement));
    }

    @Test
    void testWaitUntilContainerStartedThrowsExceptionOnMissingJdbcDriver() throws NoDriverFoundException, SQLException {
        doNothing().when(this.containerSpy).waitUntilClusterConfigurationAvailable();
        Mockito.doThrow(new NoDriverFoundException("Mock Driver-Not-Found Exception", new Exception("Mock cause")))
                .when(this.containerSpy).createConnection(anyString());
        assertThrowsLaunchException("driver was not found", () -> this.containerSpy.waitUntilContainerStarted());
    }

    @Test
    void testWithConnectTimeoutSeconds() {
        final var container = new ExasolContainer<>();
        assertThrows(UnsupportedOperationException.class, () -> container.withConnectTimeoutSeconds(1));
    }

    @Test
    void testWithStartupTimeout() {
        final var container = new ExasolContainer<>();
        assertThrows(UnsupportedOperationException.class, () -> container.withStartupTimeout(Duration.ZERO));
    }

    @Test
    void testWithExposedPorts() {
        assertThat(new ExasolContainer<>().withExposedPorts().getExposedPorts().size(), equalTo(0));
    }

    @Test
    void testWithDefaultExposedPorts() {
        assertThat(new ExasolContainer<>().getExposedPorts().size(), equalTo(2));
    }

    @Test
    void testAddExposedPorts() {
        final ExasolContainer<?> container = new ExasolContainer<>();
        container.addExposedPorts(1000);
        assertThat(container.getExposedPorts().size(), equalTo(3));
    }

    @Test
    void testCouldNotExtractPort() {
        final var container = new ExasolContainer<>("unknown:1.2.3");
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, container::configure);
        assertThat(exception.getMessage(), equalTo(
                "Could not detect internal ports for custom image. Please specify the port explicitly using withExposedPorts()."));
    }
}