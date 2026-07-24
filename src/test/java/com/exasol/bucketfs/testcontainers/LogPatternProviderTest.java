package com.exasol.bucketfs.testcontainers;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.regex.Pattern;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class LogPatternProviderTest {
    @ParameterizedTest
    @CsvSource(delimiter = ';', quoteCharacter = '\'', value = {
            "[I 220812 11:26:06 bucketfsd:228] rsync for id (('bfsdefault', 'default', 'dir1/file.txt')) is done; 'dir1/file.txt'",
            "[I 220812 11:26:06 bucketfsd:228] rsync for id (('bfsdefault', 'default', 'dir1/file.txt')) is done; '/dir1/file.txt'",
            "[I 220812 11:10:21 bucketfsd:228] rsync for id (('bfsdefault', 'default', 'dir4/file.txt')) is done; ''",
            "[I 220812 10:57:23 bucketfsd:228] rsync for id (('bfsdefault', 'default', 'dir5/sub5/file.txt')) is done; ''",
            "[I 260724 14:03:43 bucketfsd:306] rsync for id (('bfsdefault', 'default', 'this\\is\\an\\illegal\\URL')) is done; ''" })

    void testVersion8Pattern(final String logMessage, final String pathInBucket) {
        final String pattern = LogPatternProvider.VERSION_8.pattern(pathInBucket);
        assertTrue(Pattern.compile(pattern).matcher(logMessage).find(), "Log message does not match pattern: " + logMessage + " vs. " + pattern);
    }
}
