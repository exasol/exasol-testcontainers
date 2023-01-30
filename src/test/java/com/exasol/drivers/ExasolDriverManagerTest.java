package com.exasol.drivers;

import static com.exasol.drivers.ExasolDriverManager.DEFAULT_JDBC_DRIVER_PATH_IN_BUCKET;
import static com.exasol.drivers.ExasolDriverManager.MANIFEST_PATH_IN_BUCKET;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exasol.bucketfs.*;

@Tag("fast")
@ExtendWith(MockitoExtension.class)
class ExasolDriverManagerTest {
    // [utest->dsn~installing-a-jdbc-driver-from-host-filesystem~1]
    @Test
    void testInstallDriver(@Mock final Bucket bucketMock) {
        final ExasolDriverManager driverManager = new ExasolDriverManager(bucketMock);
        final DatabaseDriver driverA = JdbcDriver.builder("Foo_Driver").prefix("jdbc:foo:")
                .mainClass("org.example.FooDriver").sourceFile(Path.of("foo-driver.jar")).build();
        final DatabaseDriver driverB = JdbcDriver.builder("Bar_Driver").prefix("jdbc:bar:")
                .mainClass("org.example.BarDriver").sourceFile(Path.of("bar-driver.jar")).build();
        driverManager.install(driverA, driverB);
        assertThat(driverManager.getDrivers(), containsInAnyOrder(driverA, driverB));
    }

    // [utest->dsn~installing-a-jdbc-driver-from-host-filesystem~1]
    @Test
    void testInstallDriverFromHost(@Mock final Bucket bucketMock, @Mock final DatabaseDriver driverMock) {
        final String fileName = "driver.jar";
        final Path localPath = Path.of("/host/path/" + fileName);
        final String expectedManifest = "the_manifest";
        when(driverMock.getManifest()).thenReturn(expectedManifest);
        when(driverMock.hasSourceFile()).thenReturn(true);
        when(driverMock.getSourcePath()).thenReturn(localPath);
        when(driverMock.getFileName()).thenReturn(fileName);
        final ExasolDriverManager driverManager = new ExasolDriverManager(bucketMock);
        driverManager.install(driverMock);
        assertAll(() -> assertThat(driverManager.getDrivers(), contains(driverMock)), //
                () -> assertThat(driverManager.getManifest(), equalTo(expectedManifest)), //
                () -> verify(bucketMock).uploadFile(localPath,
                        DEFAULT_JDBC_DRIVER_PATH_IN_BUCKET + BucketConstants.PATH_SEPARATOR + fileName), //
                () -> verify(bucketMock).uploadStringContent(expectedManifest, MANIFEST_PATH_IN_BUCKET));
    }

    @Test
    void testInstallDriverHandlesBucketAccessException(@Mock final Bucket bucketMock,
            @Mock final DatabaseDriver driverMock)
            throws InterruptedException, BucketAccessException, TimeoutException, FileNotFoundException {
        final IllegalArgumentException cause = new IllegalArgumentException("the cause");
        final Throwable exception = new BucketAccessException("access denied", cause);
        assertExecptionHandled(bucketMock, driverMock, exception);
    }

    private void assertExecptionHandled(final Bucket bucketMock, final DatabaseDriver driverMock,
            final Throwable exception)
            throws InterruptedException, BucketAccessException, TimeoutException, FileNotFoundException {
        final String fileName = "irrelevant";
        final Path localPath = Path.of("/host/path/" + fileName);
        when(driverMock.hasSourceFile()).thenReturn(true);
        when(driverMock.getSourcePath()).thenReturn(localPath);
        when(driverMock.getFileName()).thenReturn(fileName);
        final ExasolDriverManager driverManager = new ExasolDriverManager(bucketMock);
        Mockito.doThrow(exception).when(bucketMock).uploadFile(ArgumentMatchers.any(), ArgumentMatchers.anyString());
        assertThrows(DriverManagerException.class, () -> driverManager.install(driverMock));
    }

    @Test
    void testInstallDriverHandlesTimeoutException(@Mock final Bucket bucketMock, @Mock final DatabaseDriver driverMock)
            throws InterruptedException, BucketAccessException, TimeoutException, FileNotFoundException {
        final Throwable exception = new TimeoutException("timed out");
        assertExecptionHandled(bucketMock, driverMock, exception);
    }
}