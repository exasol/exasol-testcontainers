package com.exasol.containers.status;

import static com.exasol.containers.ExasolService.BUCKETFS;
import static com.exasol.containers.ExasolService.UDF;
import static com.exasol.containers.status.ServiceStatus.NOT_READY;
import static com.exasol.containers.status.ServiceStatus.READY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ContainerStatusCacheTest {
    private Path cacheDirectory;
    @TempDir
    Path tempDirectory;

    @BeforeEach
    void beforeEach() {
        this.cacheDirectory = this.tempDirectory.resolve(ContainerStatusCache.CACHE_SUB_DIRECTORY);
    }

    @Test
    void testContainerStateCacheDoesNotExist() {
        final String containerId = "001122334455";
        assertThat(createContainerStateCache().isCacheAvailable(containerId), equalTo(false));
    }

    private ContainerStatusCache createContainerStateCache() {
        return new ContainerStatusCache(this.tempDirectory);
    }

    @Test
    void testContainerStateCacheExists() throws IOException {
        final String containerId = "112233445566";
        createCacheFile(containerId, "");
        assertThat(createContainerStateCache().isCacheAvailable(containerId), equalTo(true));
    }

    private void createCacheFile(final String containerId, final String content) throws IOException {
        Files.createDirectory(this.cacheDirectory);
        final Path cacheFile = this.cacheDirectory.resolve(containerId + ".cache");
        Files.write(cacheFile, content.getBytes());
    }

    @Test
    void testWriteToThenReadFromCache() {
        final String containerId = "223344556677";
        final ContainerStatus originalState = ContainerStatus.create(containerId);
        originalState.setServiceStatus(BUCKETFS, READY);
        originalState.setServiceStatus(UDF, NOT_READY);
        final ContainerStatusCache cache = createContainerStateCache();
        cache.write(containerId, originalState);
        final ContainerStatus cachedState = cache.read(containerId);
        assertThat(cachedState, equalTo(originalState));
    }
}
