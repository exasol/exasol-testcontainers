package com.exasol.containers.workarounds;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.Container.ExecResult;

import com.exasol.containers.ExasolContainer;
import com.exasol.containers.ExasolDockerImageReference;
import com.exasol.containers.exec.ExitCode;

/**
 * This is a workaround for an issue with broken log rotation present in Exasol's `docker-db` version 7.0.x and below.
 */
public class LogRotationWorkaround implements Workaround {
    static final Logger LOGGER = LoggerFactory.getLogger(LogRotationWorkaround.class);
    @SuppressWarnings("java:S1075") // This is a fixed path. It won't change.
    static final String ANACRON_SPOOL = "/var/spool/anacron/cron.daily";
    private final ExasolContainer<? extends ExasolContainer<?>> exasol;

    /**
     * Create a new instance of a {@link LogRotationWorkaround}.
     *
     * @param exasol Exasol container where the workaround is applied
     */
    public LogRotationWorkaround(final ExasolContainer<? extends ExasolContainer<?>> exasol) {
        this.exasol = exasol;
    }

    @Override
    public String getName() {
        return "log rotation";
    }

    // [impl->dsn~log-rotation-workaround-criteria~1]
    @Override
    public boolean isNecessary() {
        if (this.exasol.isReused()) {
            LOGGER.trace("Log rotation workaround unnecessary, since container is being reused.");
            return false;
        } else {
            final ExasolDockerImageReference reference = this.exasol.getDockerImageReference();
            if (reference.hasMajor() && //
                    ((reference.getMajor() < 7) //
                            || ((reference.getMajor() == 7) && reference.hasMinor() && (reference.getMinor() < 1))))

            {
                LOGGER.trace("Log rotation workaround required, since Exasol version is below 7.1.");
                return true;
            } else {
                LOGGER.trace("Log rotation workaround unnecessary, since Exasol version is 7.1 or higher.");
                return false;
            }
        }
    }

    // [impl->dsn~log-rotation-workaround~1]
    @Override
    public void apply() throws WorkaroundException {
        try {
            final ExecResult result = this.exasol.execInContainer("sh", "-c",
                    "date +%Y%m%d --date tomorrow > " + ANACRON_SPOOL);
            if (result.getExitCode() != ExitCode.OK) {
                throw new WorkaroundException("Unable to apply log rotation workaround. Error during comand execution: "
                        + result.getStderr());
            }
        } catch (final UnsupportedOperationException | IOException exception) {
            throw new WorkaroundException("Unable to apply log rotation workaround.", exception);
        } catch (final InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new WorkaroundException("Interrupted during attempt to apply log rotation workaround.");
        }
    }
}