package com.exasol.containers.nano;

import static com.exasol.containers.ExasolContainerConstants.DEFAULT_ADMIN_USER;
import static com.exasol.containers.ExasolContainerConstants.DEFAULT_SYS_USER_PASSWORD;
import static com.exasol.containers.nano.ExasolNanoContainer.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("fast")
class ExasolNanoContainerTest {
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
    void overridesCredentials() {
        try (final ExasolNanoContainer container = new ExasolNanoContainer()) {
            container.withUsername("THE_USER").withPassword("THE_PASSWORD");
            assertThat(container.getUsername(), equalTo("THE_USER"));
            assertThat(container.getPassword(), equalTo("THE_PASSWORD"));
        }
    }

    @Test
    void enablesReuse() {
        try (final ExasolNanoContainer container = new ExasolNanoContainer()) {
            container.withReuse(true);
            assertThat(container.isShouldBeReused(), equalTo(true));
        }
    }
}
