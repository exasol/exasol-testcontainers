package com.exasol.containers.slc.fileprovider;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class UrlFileProviderTest {

    @ParameterizedTest
    @CsvSource({ "http://example.com/file.txt, file.txt", "http://example.com/dir/file.txt, file.txt",
            "http://example.com/dir/file.txt?param=value, file.txt", "https://example.com/file, file",
            "file:///file, file", "file:/file, file",
            "https://extensions-internal.exasol.com/com.exasol/script-languages-release/7.1.0/template-Exasol-all-python-3.10_release.tar.gz, template-Exasol-all-python-3.10_release.tar.gz",
            "https://github.com/exasol/script-languages-release/releases/download/7.1.0/template-Exasol-all-python-3.10_release.tar.gz, template-Exasol-all-python-3.10_release.tar.gz" })
    void getUrlFileName(final URL url, final String expectedFileName) throws MalformedURLException {
        assertThat(UrlFileProvider.getUrlFileName(url), equalTo(expectedFileName));
    }
}
