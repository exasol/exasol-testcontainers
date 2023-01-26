package com.exasol.containers.ssh;

import static com.exasol.containers.ExasolContainerConstants.CACHE_DIRECTORY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.testcontainers.containers.Container.ExecResult;
import org.testcontainers.containers.ExecResultFactory;

import com.exasol.containers.ssh.DockerAccess.DockerProbe;
import com.exasol.containers.ssh.DockerAccess.SessionBuilderProvider;

// [utest->dsn~detect-if-docker-exec-is-possible~1]
// [utest->dsn~access-via-ssh~1]
class DockerAccessTest {
    @ParameterizedTest
    @CsvSource(value = { "true,DOCKER_EXEC", "false,SSH" })
    void testBuild(final boolean fileExists, final DockerAccess.Mode expectedMode) {
        final SshKeys sshKeysMock = mockSshKeys();
        final SessionBuilderProvider sessionBuilderProviderMock = mockSessionBuilderProvider();
        final DockerProbe dockerProbeMock = mockDockerProbe(fileExists);
        final DockerAccess dockerAccess = DockerAccess.builder() //
                .temporaryCredentialsDirectory(CACHE_DIRECTORY) //
                .sshKeys(sshKeysMock) //
                .sessionBuilderProvider(sessionBuilderProviderMock) //
                .dockerProbe(dockerProbeMock) //
                .build();
        assertAll( //
                () -> assertThat(dockerAccess.getSshKeys(), sameInstance(sshKeysMock)), //
                () -> assertThat(dockerAccess.getSsh(), isA(Ssh.class)), //
                () -> assertThat(dockerAccess.getMode(), equalTo(expectedMode)), //
                () -> assertThat(dockerAccess.supportsDockerExec(), equalTo(fileExists)) //
        );
    }

    private static SshKeys mockSshKeys() {
        final IdentityProvider identityProviderMock = mock(IdentityProvider.class);
        final SshKeys sshKeysMock = mock(SshKeys.class);
        when(sshKeysMock.getIdentityProvider()).thenReturn(identityProviderMock);
        return sshKeysMock;
    }

    private static SessionBuilderProvider mockSessionBuilderProvider() {
        final SessionBuilderProvider sessionBuilderProviderMock = mock(SessionBuilderProvider.class);
        final SessionBuilder sessionBuilderMock = mock(SessionBuilder.class);
        when(sessionBuilderProviderMock.get()).thenReturn(sessionBuilderMock);
        when(sessionBuilderMock.identity(any())).thenReturn(sessionBuilderMock);
        return sessionBuilderProviderMock;
    }

    private static DockerProbe mockDockerProbe(final boolean fileExists) {
        final DockerProbe dockerProbeMock = mock(DockerProbe.class);
        final ExecResult result = ExecResultFactory.result(fileExists ? 0 : 1, null, null);
        when(dockerProbeMock.probeFile(any())).thenReturn(result);
        return dockerProbeMock;
    }

    // [utest->dsn~auto-create-directory-for-temporary-credentials~1]
    @Test
    void testCreateDirectoryForTemporaryCredentials(@TempDir final Path tempDir) {
        final Path credentialsDirectory = tempDir.resolve("credentials");
        assertThat("Temporary directory for credentials must not exist before container is started in test",
                credentialsDirectory.toFile().exists(), equalTo(false));
        final SshKeys sshKeysMock = mockSshKeys();
        final SessionBuilderProvider sessionBuilderProviderMock = mockSessionBuilderProvider();
        final DockerProbe dockerProbeMock = mockDockerProbe(true);
        final DockerAccess dockerAccess = DockerAccess.builder() //
                .temporaryCredentialsDirectory(credentialsDirectory) //
                .sshKeys(sshKeysMock) //
                .sessionBuilderProvider(sessionBuilderProviderMock) //
                .dockerProbe(dockerProbeMock) //
                .build();
        // We intentionally ignore the result of the next call, because we are only interested in the side effect that
        // creates the credential directory.
        dockerAccess.getSsh();
        assertThat("Container creates credentials directory if missing", credentialsDirectory.toFile().exists(),
                equalTo(true));
    }
}
