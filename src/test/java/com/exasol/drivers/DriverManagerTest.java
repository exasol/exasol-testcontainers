package com.exasol.drivers;

import static com.exasol.drivers.ExasolDriverManager.DEFAULT_JDBC_DRIVER_PATH_IN_BUCKET;
import static com.exasol.drivers.ExasolDriverManager.MANIFEST_PATH_IN_BUCKET;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exasol.bucketfs.Bucket;

@ExtendWith(MockitoExtension.class)
class DriverManagerTest {
    // [utest->dsn~installing-a-jdbc-driver-from-host-filesystem~1]
    @Test
    void testInstallDriver(@Mock final Bucket bucketMock) {
        final ExasolDriverManager driverManager = new ExasolDriverManager(bucketMock);
        final DatabaseDriver driverA = JdbcDriver.builder("Foo_Driver").prefix("jdbc:foo:")
                .mainClass("org.example.FooDriver").build();
        final DatabaseDriver driverB = JdbcDriver.builder("Bar_Driver").prefix("jdbc:bar:")
                .mainClass("org.example.BarDriver").build();
        driverManager.install(driverA, driverB);
        assertThat(driverManager.getDrivers(), containsInAnyOrder(driverA, driverB));
    }

    // [utest->dsn~installing-a-jdbc-driver-from-host-filesystem~1]
    @Test
    void testInstallDriverFromHost(@Mock final Bucket bucketMock, @Mock final DatabaseDriver driverMock) {
        final Path localPath = Path.of("/host/path/driver.jar");
        final String expectedManifest = "the_manifest";
        when(driverMock.getManifest()).thenReturn(expectedManifest);
        when(driverMock.hasSourceFile()).thenReturn(true);
        when(driverMock.getSourcePath()).thenReturn(localPath);
        final ExasolDriverManager driverManager = new ExasolDriverManager(bucketMock);
        driverManager.install(driverMock);
        assertAll(() -> assertThat(driverManager.getDrivers(), contains(driverMock)), //
                () -> assertThat(driverManager.getManifest(), equalTo(expectedManifest)), //
                () -> verify(bucketMock).uploadFile(localPath, DEFAULT_JDBC_DRIVER_PATH_IN_BUCKET + "/"), //
                () -> verify(bucketMock).uploadStringContent(expectedManifest, MANIFEST_PATH_IN_BUCKET));
    }

    // [utest->dsn~uninstalling-a-jdbc-driver-from-host-filesystem~1]
    @Test
    void testUninstallDriver(@Mock final Bucket bucketMock) {
        final ExasolDriverManager driverManager = new ExasolDriverManager(bucketMock);
        final DatabaseDriver driverA = JdbcDriver.builder("Foo_Driver").prefix("jdbc:foo:")
                .mainClass("org.example.FooDriver").build();
        final DatabaseDriver driverB = JdbcDriver.builder("Bar_Driver").prefix("jdbc:bar:")
                .mainClass("org.example.BarDriver").build();
        driverManager.install(driverA, driverB);
        driverManager.uninstall(driverA);
        assertThat(driverManager.getDrivers(), contains(driverB));
    }

    // [utest->dsn~uninstalling-a-jdbc-driver-from-host-filesystem~1]
    @Test
    void testUnInstallDriverFromHost(@Mock final Bucket bucketMock, @Mock final DatabaseDriver driverMock) {
        final Path localPath = Path.of("/host/path/driver.jar");
        final String expectedManifest = "the_manifest";
        when(driverMock.getManifest()).thenReturn(expectedManifest);
        when(driverMock.hasSourceFile()).thenReturn(true);
        when(driverMock.getSourcePath()).thenReturn(localPath);
        final ExasolDriverManager driverManager = new ExasolDriverManager(bucketMock);
        driverManager.install(driverMock);
        driverManager.uninstall(driverMock);
        assertAll(() -> assertThat(driverManager.getDrivers(), contains(driverMock)), //
                () -> assertThat(driverManager.getManifest(), equalTo(expectedManifest)), //
                () -> verify(bucketMock)
                        .deleteFile(DEFAULT_JDBC_DRIVER_PATH_IN_BUCKET + "/" + driverMock.getFileName()), //
                () -> verify(bucketMock).deleteFile(MANIFEST_PATH_IN_BUCKET));
    }
}