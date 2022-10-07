package com.exasol.containers;

import static com.exasol.containers.ExasolContainerConstants.SSH_PORT;
import static com.exasol.containers.ExasolContainerConstants.SSH_USER;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.containers.ContainerLaunchException;
import org.testcontainers.containers.JdbcDatabaseContainer.NoDriverFoundException;

import com.exasol.containers.ssh.IdentityProvider;
import com.jcraft.jsch.Session;

@Tag("slow")
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

    @Tag("slow")
    @Test
    void testWaitUntilContainerStartedTimesOut() throws Exception {
        doReturn(1234).when(this.containerSpy).getFirstMappedDatabasePort();
        doNothing().when(this.containerSpy).waitUntilClusterConfigurationAvailable();
        doReturn(this.connectionMock).when(this.containerSpy).createConnection(any());
        when(this.connectionMock.createStatement()).thenThrow(new SQLException("Mock Exception"));
        assertThrowsLaunchException("timed out", () -> this.containerSpy.waitUntilContainerStarted());
    }

    private void assertThrowsLaunchException(final String expectedMessageFragment, final Executable executable) {
        final ContainerLaunchException exception = assertThrows(ContainerLaunchException.class, executable);
        assertThat(exception.getMessage(), containsString(expectedMessageFragment));
    }

    @Test
    void testWaitUntilContainerStartedThrowsExceptionOnMissingJdbcDriver() throws NoDriverFoundException, SQLException {
        doNothing().when(this.containerSpy).waitUntilClusterConfigurationAvailable();
        Mockito.doThrow(new NoDriverFoundException("Mock Driver-Not-Found Exception", new Exception("Mock cause")))
                .when(this.containerSpy).createConnection(anyString());
        assertThrowsLaunchException("driver was not found", () -> this.containerSpy.waitUntilContainerStarted());
    }

    @SuppressWarnings("deprecation")
    @Test
    void testWithConnectTimeoutSeconds() {
        try (final ExasolContainer<?> container = new ExasolContainer<>()) {
            assertThrows(UnsupportedOperationException.class, () -> container.withConnectTimeoutSeconds(1));
        }
    }

    @SuppressWarnings("deprecation")
    @Test
    void testWithStartupTimeout() {
        try (final ExasolContainer<?> container = new ExasolContainer<>()) {
            assertThrows(UnsupportedOperationException.class, () -> container.withStartupTimeout(Duration.ZERO));
        }
    }

    @Test
    void testWithExposedPorts() {
        try (final ExasolContainer<?> container = new ExasolContainer<>()) {
            assertThat(container.withExposedPorts().getExposedPorts().size(), equalTo(0));
        }
    }

    @Test
    void testWithDefaultExposedPorts() {
        try (final ExasolContainer<?> container = new ExasolContainer<>()) {
            assertThat(container.getExposedPorts().size(), equalTo(4));
        }
    }

    @Test
    void testAddExposedPorts() {
        try (final ExasolContainer<?> container = new ExasolContainer<>()) {
            final int countBefore = container.getExposedPorts().size();
            container.addExposedPorts(1000);
            assertThat(container.getExposedPorts().size(), equalTo(countBefore + 1));
            assertThat(container.getExposedPorts(), hasItem(1000));
        }
    }

    @Test
    void testCouldNotExtractPort() {
        try (final ExasolContainer<?> container = new ExasolContainer<>("unknown:1.2.3", false)) {
            final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    container::configure);
            assertThat(exception.getMessage(), equalTo(
                    "Could not detect internal ports for custom image. Please specify the port explicitly using withExposedPorts()."));
        }
    }

    @Test
    void testAccessConfigurationBeforeReadFromContainer() {
        try (final ExasolContainer<?> container = new ExasolContainer<>()) {
            final IllegalStateException exception = assertThrows(IllegalStateException.class,
                    container::getClusterConfiguration);
            assertThat(exception.getMessage(),
                    equalTo("Tried to access Exasol cluster configuration before it was read from the container."));
        }
    }

    @Test
    void testWithJdbcConnectionTimeout() {
        try (final ExasolContainer<?> container = new ExasolContainer<>()) {
            assertThat(container.withJdbcConnectionTimeout(123).getJdbcConnectionTimeout(), equalTo(123));
        }
    }

    @Tag("slow")
    @Test
    void testGetRpcUrl() {
        try (final ExasolContainer<?> container = new ExasolContainer<>()) {
            container.withReuse(true).start();
            final String expectedUrl = "https://" + container.getHost() + ":"
                    + container.getMappedPort(container.getDefaultInternalRpcPort()) + "/jrpc";
            assertThat(container.getRpcUrl(), equalTo(expectedUrl));
        }
    }

    @Test
    void testGetDefaultInternalRpcPortReturnsPort() {
        assertThat(this.containerSpy.getDefaultInternalRpcPort(), equalTo(443));
    }

    @Test
    void testRpcPortExposed() {
        assertThat(this.containerSpy.getExposedPorts(), hasItem(443));
    }

    @Test
    void sessionBuilder() {
        final ExasolContainer<?> testee = mock(ExasolContainer.class);
        final IdentityProvider identityProvider = mock(IdentityProvider.class);
        when(testee.getHost()).thenReturn("simulated host");
        when(testee.getMappedPort(SSH_PORT)).thenReturn(321);
        doCallRealMethod().when(testee).getSessionBuilder();
        final Session session = testee.getSessionBuilder().identity(identityProvider).build();
        assertThat(session.getHost(), equalTo("simulated host"));
        assertThat(session.getPort(), equalTo(321));
        assertThat(session.getUserName(), equalTo(SSH_USER));
    }
}