package com.exasol.clusterlogs;

import java.io.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.Container;

import com.exasol.containers.exec.ExitCode;

/**
 * Detector for pattern match in a log file.
 */
public class LogPatternDetector {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogPatternDetector.class);
    private static final Pattern LOG_ENTRY_PATTERN = Pattern
            .compile("\\[. (\\d\\d)(\\d\\d)(\\d\\d) (\\d\\d:\\d\\d:\\d\\d).*");
    private final Container<? extends Container<?>> container;
    private final String logPath;
    private final String pattern;
    private final String logNamePattern;
    private final TimeZone timeZone;

    /**
     * Create a new instance of the {@link LogPatternDetector}.
     *
     * @param container      container in which the log messages reside
     * @param logPath        path of the log file to search
     * @param logNamePattern pattern used to find the file name
     * @param pattern        regular expression pattern for which to look out
     * @param timeZone       time zone that serves as context for the short timestamps in the logs
     */
    LogPatternDetector(final Container<? extends Container<?>> container, final String logPath,
            final String logNamePattern, final String pattern, final TimeZone timeZone) {
        this.container = container;
        this.logPath = logPath;
        this.logNamePattern = logNamePattern;
        this.pattern = pattern;
        this.timeZone = timeZone;
        LOGGER.debug("Created log detector that scans for \"{}\" in \"{}/{}\" with time zone \"{}\"", pattern, logPath,
                logNamePattern, timeZone.getDisplayName());
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
     * @param afterUTC UTC point in time after which the message is relevant
     * @return {@code true} if the pattern is found in the log file
     * @throws IOException          if the underlying check mechanism caused an I/O problem
     * @throws InterruptedException if the check for a pattern was interrupted
     */
    public boolean isPatternPresentAfter(final Instant afterUTC) throws IOException, InterruptedException {
        final LocalDateTime afterLocal = convertUtcToLowResulionLocal(afterUTC);
        final Container.ExecResult result = this.container.execInContainer("find", this.logPath, //
                "-name", this.logNamePattern, //
                "-exec", "awk", "/" + this.pattern.replace("/", "\\/") + "/{a=$0}END{print a}", "{}", "+");
        if (result.getExitCode() == ExitCode.OK) {
            System.out.println("===== found lines '" + result.getStdout() + "'");
            return isLogMessageFoundAfter(result.getStdout(), afterLocal);
        } else {
            return false;
        }
    }

    private LocalDateTime convertUtcToLowResulionLocal(final Instant afterUTC) {
        final LocalDateTime localDateTime = LocalDateTime.ofInstant(afterUTC, this.timeZone.toZoneId());
        return localDateTime.withNano(0);
    }

    private boolean isLogMessageFoundAfter(final String stdout, final LocalDateTime afterLocal) throws IOException {
        try (final BufferedReader reader = new BufferedReader(new StringReader(stdout))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                final Matcher matcher = LOG_ENTRY_PATTERN.matcher(line);
                if (matcher.matches()) {
                    final String isoTimestamp = "20" + matcher.group(1) + "-" + matcher.group(2) + "-"
                            + matcher.group(3) + "T" + matcher.group(4);
                    final LocalDateTime timestamp = LocalDateTime.parse(isoTimestamp);
                    if (timestamp.isAfter(afterLocal) || timestamp.isEqual(afterLocal)) {
                        LOGGER.debug("Found matching log entry {} (after {}): {}", timestamp, afterLocal, line);
                        return true;
                    }
                    System.out.println("-------- Log entry timestamp " + timestamp + " invalid, isAfter=" + afterLocal);
                }
            }
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
                + this.logNamePattern + "\"." //
                + "\n\nActual file content: '" + getLogFileContent() + "'";
    }

    private String getLogFileContent() {
        try {
            final Container.ExecResult result = this.container.execInContainer("find", this.logPath, //
                    "-name", this.logNamePattern, //
                    "-exec", "cat", "{}", "+");
            return result.getStdout();
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.warn("Exception retrieving content of file {}", this.logPath, e);
            return "(error reading file)";
        } catch (UnsupportedOperationException | IOException e) {
            LOGGER.warn("Exception retrieving content of file {}", this.logPath, e);
            return "(error reading file)";
        }
    }
}
