package com.exasol.containers.slc.fileprovider;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.io.IOException;
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
class ChecksumVerifyingFileProviderTest {

    @Mock
    FileProvider delegateMock;

    @TempDir
    Path tempDir;

    @Test
    void getFileName() {
        final FileProvider testee = testee("checksum");
        when(delegateMock.getFileName()).thenReturn("fileName");
        assertThat(testee.getFileName(), equalTo("fileName"));
    }

    @ParameterizedTest
    @CsvSource({
            "'', wrong, cf83e1357eefb8bdf1542850d66d8007d620e4050b5715dc83f4a921d36ce9ce47d0d13c5d85f2b0ff8318d2877eec2f63b931bd47417a81a538327af927da3e",
            "file content, asdf, 2fb1877301854ac92dd518018f97407a0a88bb696bfef0a51e9efbd39917353500009e15bd72c3f0e4bf690115870bfab926565d5ad97269d922dbbb41261221" })
    // [utest->dsn~install-custom-slc.verify-checksum~1]
    void invalidChecksum(final String fileContent, final String wrongChecksum, final String actualChecksum)
            throws IOException {
        final Path path = tempDir.resolve("file");
        Files.writeString(path, fileContent);
        final FileProvider testee = testee(wrongChecksum);
        when(delegateMock.getLocalFile()).thenReturn(path);
        final IllegalStateException exception = assertThrows(IllegalStateException.class, testee::getLocalFile);
        assertThat(exception.getMessage(), equalTo("E-ETC-37: Sha512 checksum verification failed for file '" + path
                + "', expected '" + wrongChecksum + "' but got '" + actualChecksum + "'"));
    }

    @ParameterizedTest
    @CsvSource({
            "'',  cf83e1357eefb8bdf1542850d66d8007d620e4050b5715dc83f4a921d36ce9ce47d0d13c5d85f2b0ff8318d2877eec2f63b931bd47417a81a538327af927da3e",
            "'',  CF83E1357EEFB8BDF1542850D66D8007D620E4050B5715DC83F4A921D36CE9CE47D0D13C5D85F2B0FF8318D2877EEC2F63B931BD47417A81A538327AF927DA3E",
            "file content, 2fb1877301854ac92dd518018f97407a0a88bb696bfef0a51e9efbd39917353500009e15bd72c3f0e4bf690115870bfab926565d5ad97269d922dbbb41261221",
            "file content, 2FB1877301854AC92DD518018F97407A0A88BB696BFEF0A51E9EFBD39917353500009E15BD72C3F0E4BF690115870BFAB926565D5AD97269D922DBBB41261221", })
    // [utest->dsn~install-custom-slc.verify-checksum~1]
    void correctChecksum(final String fileContent, final String checksum) throws IOException {
        final Path path = tempDir.resolve("file");
        Files.writeString(path, fileContent);
        final FileProvider testee = testee(checksum);
        when(delegateMock.getLocalFile()).thenReturn(path);
        assertThat(testee.getLocalFile(), equalTo(path));
    }

    FileProvider testee(final String checksum) {
        return new ChecksumVerifyingFileProvider(delegateMock, checksum);
    }
}
