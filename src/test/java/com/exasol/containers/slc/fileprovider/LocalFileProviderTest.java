package com.exasol.containers.slc.fileprovider;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.sameInstance;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

class LocalFileProviderTest {

    // [utest->dsn~install-custom-slc.local-file~1]
    @Test
    void getLocalFile() {
        final Path path = Path.of("file");
        assertThat(new LocalFileProvider(path).getLocalFile(), sameInstance(path));
    }

    @Test
    void getFileName() {
        final Path path = Path.of("dir/file");
        assertThat(new LocalFileProvider(path).getFileName(), equalTo("file"));
    }
}
