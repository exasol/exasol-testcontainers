package com.exasol.containers.slc;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;
import java.sql.Connection;

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
            "wrong-extension.txt; E-ETC-28: File 'wrong-extension.txt' has an unsupported file extension. The following file extensions are supported for SLCs: ['.tar.gz', '.tar.bz2', '.zip'].",
            "uppercase.TAR.GZ; E-ETC-28: File 'uppercase.TAR.GZ' has an unsupported file extension. The following file extensions are supported for SLCs: ['.tar.gz', '.tar.bz2', '.zip'].",
            "uppercase.TAR.bz2; E-ETC-28: File 'uppercase.TAR.bz2' has an unsupported file extension. The following file extensions are supported for SLCs: ['.tar.gz', '.tar.bz2', '.zip'].",
            "uppercase.ZIP; E-ETC-28: File 'uppercase.ZIP' has an unsupported file extension. The following file extensions are supported for SLCs: ['.tar.gz', '.tar.bz2', '.zip'].",
            "missing.zip; E-ETC-27: Local file '/path/to/missing.zip' does not exist",
            "missing.tar.gz; E-ETC-27: Local file '/path/to/missing.tar.gz' does not exist",
            "missing.tar.bz2; E-ETC-27: Local file '/path/to/missing.tar.bz2' does not exist", })
    void validateSlc(final String fileName, final String expectedErrorMessage) {
        final ScriptLanguageContainerInstaller installer = testee();
        final ScriptLanguageContainer slc = ScriptLanguageContainer.builder()
                .localFile(Path.of("/path/to/").resolve(fileName)).alias("alias").language(Language.R).build();
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> installer.install(slc));
        assertThat(exception.getMessage(), equalTo(expectedErrorMessage));
    }

    private ScriptLanguageContainerInstaller testee() {
        return ScriptLanguageContainerInstaller.create(connectionMock, bucketMock);
    }
}
