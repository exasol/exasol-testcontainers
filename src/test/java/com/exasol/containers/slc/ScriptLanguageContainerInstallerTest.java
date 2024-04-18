package com.exasol.containers.slc;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;
import java.sql.Connection;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exasol.bucketfs.Bucket;
import com.exasol.containers.slc.ScriptLanguageContainer.Language;

@ExtendWith(MockitoExtension.class)
class ScriptLanguageContainerInstallerTest {

    @Mock
    private Connection connectionMock;
    @Mock
    private Bucket bucketMock;

    @ParameterizedTest
    @CsvSource(delimiter = ';', value = {
            "wrong-extension.txt; E-ETC-35: File 'wrong-extension.txt' has an unsupported file extension. The following file extensions are supported for SLCs: ['.tar.gz', '.tar.bz2', '.zip'].",
            "/path/to/wrong-extension.txt; E-ETC-35: File '/path/to/wrong-extension.txt' has an unsupported file extension. The following file extensions are supported for SLCs: ['.tar.gz', '.tar.bz2', '.zip'].",
            "relativePath/to/wrong-extension.txt; E-ETC-35: File 'relativePath/to/wrong-extension.txt' has an unsupported file extension. The following file extensions are supported for SLCs: ['.tar.gz', '.tar.bz2', '.zip'].",
            "uppercase.TAR.GZ; E-ETC-35: File 'uppercase.TAR.GZ' has an unsupported file extension.",
            "uppercase.TAR.bz2; E-ETC-35: File 'uppercase.TAR.bz2' has an unsupported file extension.",
            "uppercase.ZIP; E-ETC-35: File 'uppercase.ZIP' has an unsupported file extension.",
            "missing.zip; E-ETC-27: Local file 'missing.zip' does not exist",
            "/path/to/missing.zip; E-ETC-27: Local file '/path/to/missing.zip' does not exist",
            "relativePath/to/missing.zip; E-ETC-27: Local file 'relativePath/to/missing.zip' does not exist",
            "missing.tar.gz; E-ETC-27: Local file 'missing.tar.gz' does not exist",
            "missing.tar.bz2; E-ETC-27: Local file 'missing.tar.bz2' does not exist", })
    void validateLocalFile(final String path, final String expectedErrorMessage) {
        final ScriptLanguageContainerInstaller installer = testee();
        final ScriptLanguageContainer slc = ScriptLanguageContainer.builder().localFile(Path.of(path))
                .language(Language.R).build();
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> installer.install(slc));
        assertThat(exception.getMessage(), startsWith(expectedErrorMessage));
    }

    @ParameterizedTest
    @CsvSource(delimiter = ';', nullValues = "NULL", value = {
            "not-an-url; NULL; E-ETC-36: Invalid SLC URL: 'not-an-url'",
            "http://example.com; NULL; E-ETC-40: Filename '' of URL 'http://example.com' has an unsupported file extension. The following file extensions are supported for SLCs: ['.tar.gz', '.tar.bz2', '.zip'].",
            "http://example.com/; NULL; E-ETC-40: Filename '' of URL 'http://example.com/' has an unsupported file extension.",
            "http://example.com/file; NULL; E-ETC-40: Filename 'file' of URL 'http://example.com/file' has an unsupported file extension.",
            "http://example.com/path/file; NULL; E-ETC-40: Filename 'file' of URL 'http://example.com/path/file' has an unsupported file extension.",
            "http://example.com/uppercase.ZIP; NULL; E-ETC-40: Filename 'uppercase.ZIP' of URL 'http://example.com/uppercase.ZIP' has an unsupported file extension.",
            "http://example.com/uppercase.TAR.GZ; NULL; E-ETC-40: Filename 'uppercase.TAR.GZ' of URL 'http://example.com/uppercase.TAR.GZ' has an unsupported file extension.",
            "http://example.com/uppercase.TAR.BZ2; NULL; E-ETC-40: Filename 'uppercase.TAR.BZ2' of URL 'http://example.com/uppercase.TAR.BZ2' has an unsupported file extension.",
            "http://example.com/file.zip; NULL; E-ETC-42: An URL is specified but sha512sum checksum is missing",
            "http://example.com/file.tar.gz; NULL; E-ETC-42: An URL is specified but sha512sum checksum is missing",
            "http://example.com/file.tar.bz2; NULL; E-ETC-42: An URL is specified but sha512sum checksum is missing",
            "http://no-such-server.unknown.com/file.zip; checksum; E-ETC-38: Failed to download file from URL 'http://no-such-server.unknown.com/file.zip'", })
    void validateUrl(final String url, final String checksum, final String expectedErrorMessage) {
        final ScriptLanguageContainerInstaller installer = testee();
        final ScriptLanguageContainer slc = ScriptLanguageContainer.builder().url(url).sha512sum(checksum)
                .language(Language.R).build();
        final RuntimeException exception = assertThrows(RuntimeException.class, () -> installer.install(slc));
        assertThat(exception.getMessage(), startsWith(expectedErrorMessage));
    }

    @Test
    void urlAndFileAreMissing() {
        final ScriptLanguageContainerInstaller installer = testee();
        final ScriptLanguageContainer slc = ScriptLanguageContainer.builder().language(Language.R).build();
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> installer.install(slc));
        assertThat(exception.getMessage(), startsWith("E-ETC-39: SLC must have either a local file or a URL"));
    }

    @Test
    void bothUrlAndFileArePresent() {
        final ScriptLanguageContainerInstaller installer = testee();
        final ScriptLanguageContainer slc = ScriptLanguageContainer.builder().language(Language.R).url("http://url")
                .localFile(Path.of("localFile")).build();
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> installer.install(slc));
        assertThat(exception.getMessage(),
                startsWith("E-ETC-41: SLC must have either a local file or a URL, not both"));
    }

    private ScriptLanguageContainerInstaller testee() {
        return ScriptLanguageContainerInstaller.create(connectionMock, bucketMock);
    }
}
