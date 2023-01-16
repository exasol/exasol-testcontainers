package com.exasol.containers.ssh;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class DirectorySelectorTest {

    private static final String EXISTING_FOLDER = "existing";
    private static final String DEFAULT_FOLDER = "default";

    @TempDir
    Path tempDir;

    @BeforeEach
    void beforeEach() throws IOException {
        Files.createDirectory(this.tempDir.resolve(EXISTING_FOLDER));
    }

    @Test
    void testCurrentWorkingDir() {
        final Path p0 = new DirectorySelector().or("target").getPath().toAbsolutePath();
        assertThat(p0, equalTo(Path.of("target").toAbsolutePath()));
    }

    @Test
    void testNoCandidate() {
        final DirectorySelector testee = testee();
        assertThrows(NullPointerException.class, () -> testee.ensureExists());
    }

    @Test
    void testExistingFirst() {
        verifyResult(testee().ifNotNull(Path.of(EXISTING_FOLDER)), EXISTING_FOLDER);
    }

    @Test
    void testNestedDirectory() {
        verifyResult(testee().or("sub/directory"), "directory");
        assertThat(Files.isDirectory(this.tempDir.resolve("sub/directory")), is(true));
    }

    @Test
    void testExistingLastResort() {
        verifyResult(testee().or(EXISTING_FOLDER), EXISTING_FOLDER);
    }

    @Test
    void testExceptionOnCreate() throws IOException {
        final Path p = this.tempDir.resolve("file");
        Files.writeString(p, "content");
        assertThrows(UncheckedIOException.class, () -> testee().or("file").ensureExists());
    }

    @Test
    void testDefault() {
        verifyResult(testee(), DEFAULT_FOLDER);
    }

    @Test
    void testNull() {
        verifyResult(testee().ifNotNull(null), DEFAULT_FOLDER);
    }

    @Test
    void testNotNull() {
        final String other = "other";
        verifyResult(testee().ifNotNull(this.tempDir.resolve(other)), other);
    }

    @Test
    void testNonExisting() {
        verifyResult(testee().orIfExists("not-existing"), DEFAULT_FOLDER);
    }

    @Test
    void testExisting() {
        verifyResult(testee().orIfExists(EXISTING_FOLDER), EXISTING_FOLDER);
    }

    @Test
    void testComplexPreferFirst() {
        verifyResult(testee() //
                .ifNotNull(this.tempDir.resolve("first")) //
                .orIfExists("not-existing") //
                .orIfExists(EXISTING_FOLDER) //
                .or("other"), //
                "first");
    }

    @Test
    void testComplexPreferExisting() {
        verifyResult(testee() //
                .ifNotNull(null) //
                .orIfExists("not-existing") //
                .orIfExists(EXISTING_FOLDER) //
                .or("other"), //
                EXISTING_FOLDER);
    }

    @Test
    void testComplexLastResort() {
        verifyResult(testee() //
                .ifNotNull(null) //
                .orIfExists("not-existing") //
                .or("last-resort"), //
                "last-resort");
    }

    private DirectorySelector testee() {
        return new DirectorySelector(this.tempDir);
    }

    private void verifyResult(final DirectorySelector testee, final String expectedName) {
        final Path result = testee.or(DEFAULT_FOLDER).ensureExists();
        assertThat(Files.isDirectory(result), is(true));
        assertThat(result.getFileName().toString(), equalTo(expectedName));
    }
}
