package com.exasol.clusterlogs;

import static com.exasol.errorreporting.ExaError.messageBuilder;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.Container;

import com.exasol.bucketfs.monitor.BucketFsMonitor.State;
import com.exasol.bucketfs.monitor.LineCountState;
import com.exasol.bucketfs.monitor.TimestampState;
import com.exasol.containers.ExasolContainer;
import com.exasol.containers.ExasolDockerImageReference;
import com.exasol.containers.exec.ExitCode;
import com.exasol.containers.ssh.SshException;

/**
 * Detector for pattern match in a log file.
 */
public class LogPatternDetector {

    /**
     * @return builder to build a new instance of {@link LogPatternDetector}.
     */
    public static Builder builder() {
        return new Builder();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(LogPatternDetector.class);
    private ExasolContainer<? extends ExasolContainer<?>> container;
    private String logPath;
    private String pattern;
    private String logNamePattern;
    private LogEntryPatternVerifier logEntryVerifier = LogEntryPatternVerifier.ALWAYS_TRUE;
    private long afterLine = 0;

    private LogPatternDetector() {
        // use builder !
    }

    /**
     * Check whether a certain pattern appears in a log message.
     * <p>
     * <b>Implementation notes</b>
     * </p>
     * <p>
     * We use {@code find} to locate the log file(s) matching the filename search pattern. This {@code find} command
     * then executes a command that searches the files.
     * </p>
     * <p>
     * Since the testcontainers {@link Container#execInContainer(String...)} is not a full-fledged shell, we can't use
     * pipes and need {@code -exec} instead. That means the complete job of isolating the matches must be done in one
     * single command executed by {@code find}. While we could wrap our call in a shell, that would have a considerable
     * performance impact.
     * </p>
     * <p>
     * We use {@code awk} to find the <em>last</em> match against the log entry pattern in the log file. This way we
     * avoid transferring more and more data with growing logs where we find multiple matches.
     * </p>
     * <p>
     * We considered the following alternative:
     * </p>
     *
     * <pre>
     * tac | grep &lt;pattern&gt; | head -n 1
     * </pre>
     * <p>
     * But this would require pipes.
     * </p>
     *
     * @return {@code true} if the pattern is found in the log file
     * @throws IOException          if the underlying check mechanism caused an I/O problem
     * @throws InterruptedException if the check for a pattern was interrupted
     * @throws SshException         if using SSH access to docker container and remote execution of command failed
     */
    public boolean isPatternPresent() throws IOException, InterruptedException, SshException {
        final Container.ExecResult result = this.container.execInContainer("find", this.logPath, //
                "-name", this.logNamePattern, "-exec", "awk", //
                awkCommand(this.afterLine, this.pattern.replace("/", "\\/")), //
                "{}", "+");
        if (result.getExitCode() == ExitCode.OK) {
            return this.logEntryVerifier.isLogMessageFound(result.getStdout());
        } else {
            return false;
        }
    }

    private String awkCommand(final long afterLine, final String regex) {
        return "(NR>" + afterLine + ")&&" + "/" + regex + "/{a=$0}END{print a}";
    }

    /**
     * Describe the detection goal.
     *
     * @return human-readable explanation of what this detector looks for.
     */
    public String describe() {
        return "Scanning for log message pattern \"" + this.pattern + "\" in \"" + this.logPath + "/"
                + this.logNamePattern + "\", using " + this.logEntryVerifier + ".";
    }

    /**
     * Returns the complete actual log content from all matching files. This can be useful for debugging log related
     * issues.
     * <p>
     * Note that the order in which log files are read is not defined.
     *
     * @return complete actual log content from all matching files.
     */
    public String getActualLog() {
        try {
            final Container.ExecResult result = this.container.execInContainer("find", this.logPath, //
                    "-name", this.logNamePattern, //
                    "-exec", "cat", "{}", "+");
            return result.getStdout();
        } catch (final InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("InterruptedException when reading log file content", exception);
        } catch (final IOException exception) {
            throw new UncheckedIOException(messageBuilder("F-ETC-6")
                    .message("Exception reading content of file(s) {{logPath}}/{{logNamePattern}}", this.logPath,
                            this.logNamePattern)
                    .toString(), exception);
        }
    }

    static class Builder {
        private final LogPatternDetector detector = new LogPatternDetector();

        public Builder container(final ExasolContainer<? extends ExasolContainer<?>> value) {
            this.detector.container = value;
            return this;
        }

        public Builder logPath(final String value) {
            this.detector.logPath = value;
            return this;
        }

        public Builder pattern(final String value) {
            this.detector.pattern = value;
            return this;
        }

        public Builder logNamePattern(final String value) {
            this.detector.logNamePattern = value;
            return this;
        }

        public Builder forState(final State state) {
            if (state instanceof LineCountState) {
                return afterLine(((LineCountState) state).getLineNumber());
            }
            if (state instanceof TimestampState) {
                return logEntryVerifier(new TimestampLogEntryPatternVerifier(state, getTimezone()));
            }
            throw new IllegalArgumentException("Unsupported class " + state.getClass() + " of state");
        }

        /**
         * Bucketfsd in Exasol 8 does not use the timezone setting from {@code /exa/etc/EXAConf} but hard-coded python
         * {@code time.gmtime}.
         *
         * @return timezone setting for bucketfs logs
         */
        private TimeZone getTimezone() {
            final ExasolDockerImageReference image = this.detector.container.getDockerImageReference();
            if (image.hasMajor() && (image.getMajor() < 8)) {
                return this.detector.container.getClusterConfiguration().getTimeZone();
            } else {
                return TimeZone.getTimeZone("UTC");
            }
        }

        public Builder logEntryVerifier(final LogEntryPatternVerifier value) {
            this.detector.logEntryVerifier = value;
            return this;
        }

        public Builder afterLine(final long value) {
            this.detector.afterLine = value;
            return this;
        }

        public LogPatternDetector build() {
            LOGGER.trace("Created log detector that scans for \"{}\" in \"{}/{}\"" //
                    + " after line {} with verifier {}", //
                    this.detector.pattern, //
                    this.detector.logPath, //
                    this.detector.logNamePattern, //
                    this.detector.afterLine, //
                    this.detector.logEntryVerifier);
            return this.detector;
        }
    }
}
