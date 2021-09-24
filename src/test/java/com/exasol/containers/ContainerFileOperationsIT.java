package com.exasol.containers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.*;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.exasol.testutil.ExceptionAssertions;

@Tag("slow")
@Testcontainers
class ContainerFileOperationsIT {
    @Container
    private static final ExasolContainer<? extends ExasolContainer<?>> CONTAINER = new ExasolContainer<>()
            .withRequiredServices().withReuse(true);

    private ContainerFileOperations containerFileOperations;

    @BeforeEach
    void setup() {
        this.containerFileOperations = new ContainerFileOperations(CONTAINER);
    }

    @Test
    void testReadExistingFile() throws ExasolContainerException {
        final String exaConfContent = this.containerFileOperations.readFile("/exa/etc/EXAConf", StandardCharsets.UTF_8);
        assertThat(exaConfContent, allOf( //
                startsWith("[Global]"), //
                containsString("Cert = /exa/etc/ssl/ssl.crt")));
    }

    @Test
    void testReadMissingFileFails() {
        ExceptionAssertions.assertThrowsWithMessage(ExasolContainerException.class,
                () -> this.containerFileOperations.readFile("/noSuchFile", StandardCharsets.UTF_8),
                "F-ETC-10: Unable to read file '/noSuchFile' from container. Error message: 'cat: /noSuchFile: No such file or directory'.");
    }
}
