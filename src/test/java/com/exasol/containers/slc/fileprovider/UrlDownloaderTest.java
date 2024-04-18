package com.exasol.containers.slc.fileprovider;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class UrlDownloaderTest {

    @TempDir
    Path tempDir;
    private Path targetPath;

    @BeforeEach
    void setup() {
        targetPath = tempDir.resolve("file");
    }

    @Test
    void downloadFromInvalidUrl() {
        final UncheckedIOException exception = assertThrows(UncheckedIOException.class,
                () -> download("http://invalid-url"));
        assertThat(exception.getMessage(),
                equalTo("E-ETC-38: Failed to download file from URL 'http://invalid-url' to target path '" + targetPath
                        + "': java.net.UnknownHostException: invalid-url"));
    }

    // [utest->dsn~install-custom-slc.url~1]
    @Test
    void downloadFromValidUrl() throws IOException {
        download("https://example.com");
        assertThat(Files.readString(targetPath), containsString("<title>Example Domain</title>"));
    }

    private void download(final String url) throws MalformedURLException {
        new UrlDownloader().download(new URL(url), targetPath);
    }
}
