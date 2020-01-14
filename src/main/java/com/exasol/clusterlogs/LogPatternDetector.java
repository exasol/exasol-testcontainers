package com.exasol.clusterlogs;

import java.io.*;
import java.time.Instant;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.Container;

/**
 * Detector for pattern match in a log file.
 */
public class LogPatternDetector {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogPatternDetector.class);
    private static final int EXIT_OK = 0;
    private static final Pattern LOG_ENTRY_PATTERN = Pattern
            .compile("\\[. (\\d\\d)(\\d\\d)(\\d\\d) (\\d\\d:\\d\\d:\\d\\d).*");
    private final Container<? extends Container<?>> container;
    private final String logPath;
    private final String pattern;
    private final String logNamePattern;

    /**
     * Create a new instance of the {@link LogPatternDetector}
     *
     * @param container      container in which the log messages reside
     * @param logPath        path of the log file to search
     * @param logNamePattern pattern used to find the file name
     * @param pattern        regular expression pattern for which to look out
     */
    public LogPatternDetector(final Container<? extends Container<?>> container, final String logPath,
            final String logNamePattern, final String pattern) {
        this.container = container;
        this.logPath = logPath;
        this.logNamePattern = logNamePattern;
        this.pattern = pattern;
        LOGGER.info("Created log detector that scans for \"{}\" in \"{}/{}\"", pattern, logPath, logNamePattern);
    }

    /**
     * Check whether a certain pattern appears in a log message.
     *
     * @param afterUTC UTC point in time after which the message is relevant
     * @return {@code true} if the pattern is found in the log file
     * @throws IOException          if the underlying check mechanism caused an I/O problem
     * @throws InterruptedException if the check for a pattern was interrupted
     */
    public boolean isPatternPresentAfter(final Instant afterUTC) throws IOException, InterruptedException {
        final Container.ExecResult result = this.container.execInContainer("find", this.logPath, //
                "-name", this.logNamePattern, //
                "-exec", "grep", this.pattern, "{}", "+");
        if (result.getExitCode() == EXIT_OK) {
            return isLogMessageFoundAfter(result.getStdout(), afterUTC);
        } else {
            return false;
        }
    }

    private boolean isLogMessageFoundAfter(final String stdout, final Instant afterUTC) throws IOException {
        try (final BufferedReader reader = new BufferedReader(new StringReader(stdout))) {
            String line;
            while ((line = reader.readLine()) != null) {
                final Matcher matcher = LOG_ENTRY_PATTERN.matcher(line);
                if (matcher.matches()) {
                    final String isoTimestamp = "20" + matcher.group(1) + "-" + matcher.group(2) + "-"
                            + matcher.group(3) + "T" + matcher.group(4) + "Z";
                    final Instant timestamp = Instant.parse(isoTimestamp);
                    if (timestamp.isAfter(afterUTC)) {
                        return true;
                    }
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
                + this.logNamePattern + "\".";
    }
}