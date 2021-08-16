package com.exasol.containers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.in;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.net.*;
import java.util.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
class HostIpDetectorIT {
    @Container
    private static final ExasolContainer<? extends ExasolContainer<?>> EXASOL = new ExasolContainer<>().withReuse(true);

    // [itest->dsn~host-ip-address-detection~1]
    @Test
    @EnabledOnOs({ OS.LINUX })
    void testDetectorOnLinux() throws UnknownHostException, SocketException {
        final String detectedIp = EXASOL.getHostIp();
        final List<String> nonLocalHostAddresses = collectHostAddresses();
        final String localhostIP = InetAddress.getLocalHost().getHostAddress();
        assertAll(() -> assertThat("Not localhost", detectedIp, not(localhostIP)),
                () -> assertThat("Detected IP matches one of the non-local host IP", detectedIp,
                        in(nonLocalHostAddresses)));
    }

    private List<String> collectHostAddresses() throws SocketException, UnknownHostException {
        final List<String> hostAddresses = new ArrayList<>();
        final Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
        while (networkInterfaces.hasMoreElements()) {
            final NetworkInterface networkInterface = networkInterfaces.nextElement();
            collectAllNetworkInterfaceAddresses(networkInterface, hostAddresses);
        }
        return hostAddresses;
    }

    private void collectAllNetworkInterfaceAddresses(final NetworkInterface networkInterface,
            final List<String> hostAddresses) {
        final Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
        while (addresses.hasMoreElements()) {
            final InetAddress address = addresses.nextElement();
            final String foundAddress = address.getHostAddress();
            hostAddresses.add(foundAddress);
        }
    }
}