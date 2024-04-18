package com.exasol.containers.slc.fileprovider;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CachingUrlFileProviderTest {

    @Mock
    UrlDownloader urlDownloaderMock;

    @TempDir
    Path tempDir;

    @ParameterizedTest
    @CsvSource({ "http://example.com/file.txt, file.txt", "http://example.com/dir/file.txt, file.txt",
            "http://example.com/dir/file.txt?param=value, file.txt", "https://example.com/file, file",
            "file:///file, file", "file:/file, file",
            "https://extensions-internal.exasol.com/com.exasol/script-languages-release/7.1.0/template-Exasol-all-python-3.10_release.tar.gz, template-Exasol-all-python-3.10_release.tar.gz",
            "https://github.com/exasol/script-languages-release/releases/download/7.1.0/template-Exasol-all-python-3.10_release.tar.gz, template-Exasol-all-python-3.10_release.tar.gz" })
    void getFileName(final URL url, final String expectedFileName) throws MalformedURLException {
        assertThat(testee(url, tempDir).getFileName(), equalTo(expectedFileName));
    }

    @Test
    void createsCacheDirIfNotExists() throws MalformedURLException {
        final Path cacheDir = tempDir.resolve("cache/dir/another/dir");
        testee(new URL("http://url"), cacheDir).getLocalFile();
        assertThat(Files.exists(cacheDir), is(true));
    }

    @Test
    void doesNotcreateCacheDirIfExists() throws MalformedURLException {
        final Path cacheDir = tempDir;
        testee(new URL("http://url"), cacheDir).getLocalFile();
        assertThat(Files.exists(cacheDir), is(true));
    }

    // [utest->dsn~install-custom-slc.url~1]
    @Test
    void downloadFileIfMissing() throws MalformedURLException {
        final URL url = new URL("http://url/file");
        testee(url, tempDir).getLocalFile();
        verify(urlDownloaderMock).download(url, tempDir.resolve("file"));
    }

    @Test
    void doesNotDownloadFileIfExists() throws IOException {
        Files.createFile(tempDir.resolve("file"));
        final URL url = new URL("http://url/file");
        testee(url, tempDir).getLocalFile();
        verify(urlDownloaderMock, never()).download(any(URL.class), any(Path.class));
    }

    private CachingUrlFileProvider testee(final URL url, final Path cacheDir) {
        return new CachingUrlFileProvider(urlDownloaderMock, cacheDir, url);
    }
}
