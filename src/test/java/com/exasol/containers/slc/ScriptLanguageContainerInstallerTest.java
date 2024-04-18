package com.exasol.containers.slc;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exasol.bucketfs.Bucket;
import com.exasol.bucketfs.BucketAccessException;
import com.exasol.containers.slc.ScriptLanguageContainer.Language;

@ExtendWith(MockitoExtension.class)
class ScriptLanguageContainerInstallerTest {

    @Mock
    Connection connectionMock;
    @Mock
    Bucket bucketMock;
    @Mock
    SlcConfigurator slcConfiguratorMock;
    @TempDir
    Path tempDir;

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

    @Test
    void uploadFiles(@TempDir final Path tempDir) throws IOException, BucketAccessException, TimeoutException {
        final Path file = tempDir.resolve("file.zip");
        Files.createFile(file);
        doThrow(new BucketAccessException("expected")).when(bucketMock).uploadFile(file, "file.zip");
        final ScriptLanguageContainer slc = ScriptLanguageContainer.builder().language(Language.PYTHON).localFile(file)
                .build();
        final ScriptLanguageContainerInstaller installer = testee();

        final IllegalStateException exception = assertThrows(IllegalStateException.class, () -> installer.install(slc));
        assertThat(exception.getMessage(),
                equalTo("E-ETC-34: Failed to upload local file file '" + file + "' to bucket at 'file.zip'."));
    }

    @ParameterizedTest
    @ValueSource(strings = { ".zip", ".tar.gz", ".tar.bz2" })
    // [utest->dsn~install-custom-slc~1]
    void install(final String fileExtension) throws IOException, BucketAccessException, TimeoutException {
        final Path file = tempDir.resolve("file" + fileExtension);
        Files.createFile(file);
        final SlcConfiguration config = SlcConfiguration.parse("");
        when(slcConfiguratorMock.read()).thenReturn(config);

        testee().install(ScriptLanguageContainer.builder().language(Language.PYTHON).localFile(file).build());

        assertAll(() -> assertThat(config.format(), equalTo(
                "PYTHON3=localzmq+protobuf:///bfsdefault/default/file?lang=python#buckets/bfsdefault/default/file/exaudf/exaudfclient_py3")),
                () -> verify(bucketMock).uploadFile(file, file.getFileName().toString()),
                () -> verify(slcConfiguratorMock).write(same(config)));
    }

    private ScriptLanguageContainerInstaller testee() {
        return new ScriptLanguageContainerInstaller(bucketMock, slcConfiguratorMock, new SlcUrlFormatter());
    }
}
