package com.exasol.containers.nano;

import static com.exasol.containers.ExasolContainerConstants.DEFAULT_ADMIN_USER;
import static com.exasol.containers.ExasolContainerConstants.DEFAULT_SYS_USER_PASSWORD;
import static com.exasol.containers.nano.ExasolNanoContainer.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("fast")
class ExasolNanoContainerTest {
    private static final String JDBC_URL = "jdbc:exa:localhost:12345;fingerprint=abc;";

    @Test
    void defaultConstructorUsesNanoImage() {
        try (final ExasolNanoContainer container = new ExasolNanoContainer()) {
            assertThat(container.getImage().toString(),
                    containsString("imageName=" + EXASOL_NANO_DOCKER_IMAGE_REFERENCE));
        }
    }

    @Test
    void constructorUsesCustomImage() {
        try (final ExasolNanoContainer container = new ExasolNanoContainer("registry.example/exasol/nano:test")) {
            assertThat(container.getImage().toString(), containsString("imageName=registry.example/exasol/nano:test"));
        }
    }

    @Test
    void exposesSqlAndWebUiPorts() {
        try (final ExasolNanoContainer container = new ExasolNanoContainer()) {
            assertThat(container.getExposedPorts(), containsInAnyOrder(EXASOL_NANO_SQL_PORT, EXASOL_NANO_WEB_UI_PORT));
        }
    }

    @Test
    void setsSharedMemorySize() {
        try (final ExasolNanoContainer container = new ExasolNanoContainer()) {
            assertThat(container.getShmSize(), equalTo(EXASOL_NANO_SHARED_MEMORY_SIZE));
        }
    }

    @Test
    void doesNotUsePrivilegedMode() {
        try (final ExasolNanoContainer container = new ExasolNanoContainer()) {
            assertThat(container.isPrivilegedMode(), equalTo(false));
        }
    }

    @Test
    void usesDefaultCredentials() {
        try (final ExasolNanoContainer container = new ExasolNanoContainer()) {
            assertThat(container.getUsername(), equalTo(DEFAULT_ADMIN_USER));
            assertThat(container.getPassword(), equalTo(DEFAULT_SYS_USER_PASSWORD));
        }
    }

    @Test
    void enablesReuse() {
        try (final ExasolNanoContainer container = new ExasolNanoContainer()) {
            container.withReuse(true);
            assertThat(container.isShouldBeReused(), equalTo(true));
        }
    }

    @Test
    void constructUrlForConnectionReturnsBaseUrlForEmptyQueryString() {
        try (final ExasolNanoContainer container = new TestableExasolNanoContainer(JDBC_URL)) {
            assertThat(container.constructUrlForConnection(""), equalTo(JDBC_URL));
        }
    }

    @Test
    void constructUrlForConnectionFailsWhenQueryStringDoesNotStartWithSemicolon() {
        try (final ExasolNanoContainer container = new TestableExasolNanoContainer(JDBC_URL)) {
            final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> container.constructUrlForConnection("clientname=test"));
            assertThat(exception.getMessage(), equalTo("The ';' character must be included"));
        }
    }

    @Test
    void constructUrlForConnectionAppendsQueryStringToUrlEndingWithSemicolon() {
        try (final ExasolNanoContainer container = new TestableExasolNanoContainer(JDBC_URL)) {
            assertThat(container.constructUrlForConnection(";clientname=test"),
                    equalTo("jdbc:exa:localhost:12345;fingerprint=abc;clientname=test"));
        }
    }

    @Test
    void constructUrlForConnectionAppendsQueryStringToUrlWithoutTrailingSemicolon() {
        try (final ExasolNanoContainer container = new TestableExasolNanoContainer("jdbc:exa:localhost:12345")) {
            assertThat(container.constructUrlForConnection(";clientname=test"),
                    equalTo("jdbc:exa:localhost:12345;clientname=test"));
        }
    }

    private static class TestableExasolNanoContainer extends ExasolNanoContainer {
        private final String jdbcUrl;

        private TestableExasolNanoContainer(final String jdbcUrl) {
            this.jdbcUrl = jdbcUrl;
        }

        @Override
        public String getJdbcUrl() {
            return this.jdbcUrl;
        }
    }
}
