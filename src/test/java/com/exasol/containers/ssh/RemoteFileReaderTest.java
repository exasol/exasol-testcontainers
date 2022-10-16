package com.exasol.containers.ssh;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class RemoteFileReaderTest {

    private static final Charset CHARSET = StandardCharsets.UTF_8;

    @ParameterizedTest
    @ValueSource(ints = { 0, 1, 10, 1500, 3000 })
    void filesize(final int size) throws IOException {
        final String input = "0123456789".repeat(200);
        assertThat(read(input, size), equalTo(input.substring(0, Math.min(size, input.length()))));
    }

    private String read(final String input, final int filesize) throws IOException {
        final RemoteFileReader testee = new RemoteFileReader(null);
        try (final InputStream is = new ByteArrayInputStream(input.getBytes(CHARSET))) {
            return testee.process(is, CHARSET, filesize);
        }
    }
}
