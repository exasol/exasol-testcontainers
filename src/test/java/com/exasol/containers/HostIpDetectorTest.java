package com.exasol.containers;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.containers.Container;

import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.NetworkSettings;

@ExtendWith(MockitoExtension.class)
class HostIpDetectorTest {
    @Test
    void testGetHostIpThrowsExceptionOnEmptyNetwork(@Mock final Container<? extends Container<?>> container,
            @Mock final InspectContainerResponse containerInfo, //
            @Mock final NetworkSettings networkSettings) {
        when(container.getContainerInfo()).thenReturn(containerInfo);
        when(containerInfo.getNetworkSettings()).thenReturn(networkSettings);
        when(networkSettings.getNetworks()).thenReturn(Collections.emptyMap());
        final HostIpDetector detector = new HostIpDetector(container);
        assertThrows(IllegalStateException.class, () -> detector.getHostIp());
    }
}