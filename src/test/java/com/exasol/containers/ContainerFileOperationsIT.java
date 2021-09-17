package com.exasol.containers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.*;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Tag("slow")
@Testcontainers
class ContainerFileOperationsIT {

    @Container
    private static final ExasolContainer<? extends ExasolContainer<?>> CONTAINER = new ExasolContainer<>()
            .withReuse(true);
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
        final ExasolContainerException exception = assertThrows(ExasolContainerException.class,
                () -> this.containerFileOperations.readFile("/noSuchFile", StandardCharsets.UTF_8));
        assertThat(exception.getMessage(),
                equalTo("Error reading file '/noSuchFile': 'cat: /noSuchFile: No such file or directory'"));
    }
}
