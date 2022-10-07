package com.exasol.containers.ssh;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.testcontainers.containers.Container.ExecResult;
import org.testcontainers.containers.ExecResultFactory;

import com.exasol.containers.ssh.DockerAccess.DockerProber;
import com.exasol.containers.ssh.DockerAccess.SessionBuilderProvider;

public class DockerAccessTest {

    @ParameterizedTest
    @CsvSource(value = { "true,DOCKER_EXEC", "false,SSH" })
    void test(final boolean fileExists, final DockerAccess.Mode expectedMode) {
        final SshKeys sshKeys = mock(SshKeys.class);
        final IdentityProvider identityProvider = mock(IdentityProvider.class);
        when(sshKeys.getIdentityProvider()).thenReturn(identityProvider);

        final SessionBuilderProvider sessionBuilderProvider = mock(SessionBuilderProvider.class);
        final SessionBuilder sessionBuilder = mock(SessionBuilder.class);
        when(sessionBuilderProvider.get()).thenReturn(sessionBuilder);
        when(sessionBuilder.identity(any())).thenReturn(sessionBuilder);

        final DockerProber dockerProber = mock(DockerProber.class);
        final ExecResult result = ExecResultFactory.result(fileExists ? 0 : 1, null, null);
        when(dockerProber.probeFile(any())).thenReturn(result);

        final DockerAccess testee = DockerAccess.builder() //
                .sshKeys(sshKeys) //
                .sessionBuilderProvider(sessionBuilderProvider) //
                .dockerProber(dockerProber) //
                .build();
        assertThat(testee.getSshKeys(), sameInstance(sshKeys));
        assertThat(testee.getSsh(), isA(Ssh.class));
        assertThat(testee.getMode(), equalTo(expectedMode));
        assertThat(testee.supportsDockerExec(), equalTo(fileExists));
    }
}
