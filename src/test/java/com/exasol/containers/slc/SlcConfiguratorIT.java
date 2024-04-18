package com.exasol.containers.slc;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.exasol.containers.ExasolContainer;

@Testcontainers
class SlcConfiguratorIT {

    @Container
    static ExasolContainer<? extends ExasolContainer<?>> container = new ExasolContainer<>().withReuse(true);

    @Test
    void readConfig() {
        final SlcConfiguration config = testee().read();
        assertThat(config.format(), containsString("R=builtin_r JAVA=builtin_java PYTHON3=builtin_python3"));
    }

    @Test
    void writeConfig() {
        final SlcConfigurator testee = testee();
        final SlcConfiguration config = testee.read();
        final String uniqueAlias = "ALIAS_" + System.currentTimeMillis();
        config.setAlias(uniqueAlias, "value");
        testee.write(config);
        assertThat(testee.read().format(), containsString(uniqueAlias + "=value"));
    }

    private SlcConfigurator testee() {
        return new SlcConfigurator(container.createConnection());
    }
}
