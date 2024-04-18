package com.exasol.containers.status;

import static com.exasol.containers.ExasolService.BUCKETFS;
import static com.exasol.containers.status.ServiceStatus.NOT_CHECKED;
import static com.exasol.containers.status.ServiceStatus.READY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.nio.file.Path;
import java.util.Set;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.exasol.containers.slc.ScriptLanguageContainer;
import com.exasol.containers.slc.ScriptLanguageContainer.Builder;
import com.exasol.containers.slc.ScriptLanguageContainer.Language;
import com.exasol.testutil.SerializableVerifier;
import com.jparams.verifier.tostring.ToStringVerifier;

import nl.jqno.equalsverifier.EqualsVerifier;

@Tag("fast")
class ContainerStatusTest {

    @Test
    void testEqualsContract() {
        EqualsVerifier.forClass(ContainerStatus.class).verify();
    }

    @Test
    void testToString() {
        ToStringVerifier.forClass(ContainerStatus.class).verify();
    }

    @Test
    void testSerializable() {
        SerializableVerifier.assertSerializable(ContainerStatus.class, ContainerStatus.create("container"));
    }

    @Test
    void testGetContainerId() throws Exception {
        final String containerId = "the_id";
        final ContainerStatus status = ContainerStatus.create(containerId);
        assertThat(status.getContainerId(), equalTo(containerId));
    }

    @Test
    void testServiceStatusNotCheckedByDefault() throws Exception {
        assertThat(ContainerStatus.create("irrelevant").getServiceStatus(BUCKETFS), equalTo(NOT_CHECKED));
    }

    @Test
    void testSetServiceStatus() throws Exception {
        final ContainerStatus status = ContainerStatus.create("irrelevant");
        status.setServiceStatus(BUCKETFS, READY);
        assertThat(status.getServiceStatus(BUCKETFS), equalTo(READY));
    }

    @CsvSource({ "NOT_CHECKED, false", "NOT_READY, false", "READY, true" })
    @ParameterizedTest
    void testIsServiceReady(final ServiceStatus serviceStatus, final boolean ready) {
        final ContainerStatus status = ContainerStatus.create("irrelevant");
        status.setServiceStatus(BUCKETFS, serviceStatus);
        assertThat(status.isServiceReady(BUCKETFS), equalTo(ready));
    }

    @Test
    void testAddAllAppliedWorkarounds() {
        final ContainerStatus status = ContainerStatus.create("irrelevant");
        status.addAllAppliedWorkarounds(Set.of("A", "B"));
        assertThat(status.getAppliedWorkarounds(), containsInAnyOrder("A", "B"));
    }

    @Test
    void testContainsNoSlc() {
        final ContainerStatus status = ContainerStatus.create("irrelevant");
        assertThat(status.isInstalled(ScriptLanguageContainer.builder().language(Language.JAVA).alias("java17")
                .localFile(Path.of("java17.tar.gz")).build()), is(false));
    }

    // [utest->dsn~install-custom-slc.only-if-required~1]
    @Test
    void testInstalledSlc() {
        final ContainerStatus status = ContainerStatus.create("irrelevant");
        final Builder slcBuilder = ScriptLanguageContainer.builder().language(Language.JAVA).alias("java17")
                .localFile(Path.of("java17.tar.gz"));
        status.addInstalledSlc(slcBuilder.build());
        assertThat(status.isInstalled(slcBuilder.build()), is(true));
    }

    // [utest->dsn~install-custom-slc.only-if-required~1]
    @Test
    void testInstalledSlcDifferentValue() {
        final ContainerStatus status = ContainerStatus.create("irrelevant");
        final Builder slcBuilder = ScriptLanguageContainer.builder().language(Language.JAVA).alias("java17")
                .localFile(Path.of("java17.tar.gz"));
        status.addInstalledSlc(slcBuilder.build());
        assertThat(status.isInstalled(slcBuilder.alias("otherAlias").build()), is(false));
    }
}
