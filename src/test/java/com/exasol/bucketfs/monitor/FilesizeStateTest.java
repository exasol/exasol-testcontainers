package com.exasol.bucketfs.monitor;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

class FilesizeStateTest {

    private static final long LINE = 101;
    private static final FilesizeState TESTEE = new FilesizeState(LINE);

    @Test
    void lowResolution() {
        assertThat(TESTEE.getLineNumber(), equalTo(LINE));
        assertThat(TESTEE.toString(), equalTo("line number " + LINE));
    }

    @Test
    void accepts() {
        assertThat(TESTEE.accepts(new FilesizeState(LINE)), is(false));
        assertThat(TESTEE.accepts(new FilesizeState(LINE + 1)), is(true));
    }
}
