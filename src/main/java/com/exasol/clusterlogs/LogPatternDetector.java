package com.exasol.clusterlogs;

import java.io.IOException;
import java.io.UncheckedIOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.Container;

import com.exasol.containers.exec.ExitCode;

/**
 * Detector for pattern match in a log file.
 */
public class LogPatternDetector {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogPatternDetector.class);
    private final Container<? extends Container<?>> container;
    private final String logPath;
    private final String pattern;
    private final String logNamePattern;
    private final LogEntryPatternVerifier logEntryVerifier;

    /**
     * Create a new instance of the {@link LogPatternDetector}.
     *
     * @param container      container in which the log messages reside
     * @param logPath        path of the log file to search
     * @param logNamePattern pattern used to find the file name
     * @param pattern        regular expression pattern for which to look out
     */
    LogPatternDetector(final Container<? extends Container<?>> container, final String logPath,
            final String logNamePattern, final String pattern, final LogEntryPatternVerifier logEntryVerifier) {
        this.container = container;
        this.logPath = logPath;
        this.logNamePattern = logNamePattern;
        this.pattern = pattern;
        this.logEntryVerifier = logEntryVerifier;
        LOGGER.debug("Created log detector that scans for \"{}\" in \"{}/{}\" with verifier {}", pattern, logPath,
                logNamePattern, logEntryVerifier);
    }

    /**
     * Check whether a certain pattern appears in a log message.
     * <h2>Implementation notes</h2>
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
     */
    public boolean isPatternPresent() throws IOException, InterruptedException {
        final Container.ExecResult result = this.container.execInContainer("find", this.logPath, //
                "-name", this.logNamePattern, //
                "-exec", "awk", "/" + this.pattern.replace("/", "\\/") + "/{a=$0}END{print a}", "{}", "+");
        if (result.getExitCode() == ExitCode.OK) {
            return this.logEntryVerifier.isLogMessageFound(result.getStdout());
        } else {
            return false;
        }
    }

    /**
     * Describe the detection goal.
     *
     * @return human-readable explanation of what this detector looks for.
     */
    public String describe() {
        return "Scanning for log message pattern \"" + this.pattern + " in \"" + this.logPath + "/"
                + this.logNamePattern + "\". using " + this.logEntryVerifier;
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
            throw new UncheckedIOException(
                    "Exception reading content of file(s) " + this.logPath + "/" + this.logNamePattern, exception);
        }
    }
}
