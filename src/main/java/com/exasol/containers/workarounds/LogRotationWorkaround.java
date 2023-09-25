package com.exasol.containers.workarounds;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.Container.ExecResult;

import com.exasol.containers.ExasolContainer;
import com.exasol.containers.ExasolDockerImageReference;
import com.exasol.containers.exec.ExitCode;
import com.exasol.containers.ssh.SshException;

/**
 * This is a workaround for an issue with broken log rotation present in Exasol's `docker-db` version 7.0.x and below.
 */
public class LogRotationWorkaround implements Workaround {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogRotationWorkaround.class);
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
            return false;
        } else {
            final ExasolDockerImageReference reference = this.exasol.getDockerImageReference();
            if (reference.hasMajor() && //
                    ((reference.getMajor() < 7) //
                            || ((reference.getMajor() == 7) && reference.hasMinor() && (reference.getMinor() < 1)))) {
                LOGGER.trace("Log rotation workaround required, since Exasol version is below 7.1.");
                return true;
            } else {
                return false;
            }
        }
    }

    // [impl->dsn~log-rotation-workaround~1]
    @Override
    public void apply() throws WorkaroundException {
        try {
            final ExecResult result = this.exasol.execInContainer("sed", "-i", "-es/'bucketfsd[^']*log' //",
                    "/etc/cron.daily/exa-logrotate");
            if (result.getExitCode() != ExitCode.OK) {
                throw new WorkaroundException(
                        "Unable to apply log rotation workaround. Error during command execution: "
                                + result.getStderr());
            }
        } catch (final UnsupportedOperationException | IOException | SshException exception) {
            throw new WorkaroundException("Unable to apply log rotation workaround.", exception);
        } catch (final InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new WorkaroundException("Interrupted during attempt to apply log rotation workaround.");
        }
    }
}