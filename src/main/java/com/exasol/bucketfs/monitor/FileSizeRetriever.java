package com.exasol.bucketfs.monitor;

import java.io.*;
import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.Container;

import com.exasol.bucketfs.monitor.StateBasedBucketFsMonitor.State;
import com.exasol.clusterlogs.LineNumberLogEntryPatternVerifier;
import com.exasol.containers.ExasolContainer;

/**
 * Retrieves the {@link State} represented by the size of the log file in number of lines.
 */
public class FileSizeRetriever implements StateBasedBucketFsMonitor.StateRetriever {

    private static final Logger LOGGER = LoggerFactory.getLogger(LineNumberLogEntryPatternVerifier.class);
    private static final Pattern LINE_COUNT = Pattern.compile(" *(\\d+) .*");

    private final ExasolContainer<? extends Container<?>> container;
    private final String logPath;
    private final String logNamePattern;

    /**
     * @param container
     * @param logPath
     * @param logNamePattern
     */
    public FileSizeRetriever(final ExasolContainer<? extends Container<?>> container, final String logPath,
            final String logNamePattern) {
        this.container = container;
        this.logPath = logPath;
        this.logNamePattern = logNamePattern;
    }

    @Override
    public State getState() {
        return new LineNumberBasedState(numberOfLines());
    }

    private long numberOfLines() {
        try {
            return parseWcOutput(runWcCommand());
        } catch (UnsupportedOperationException | IOException | InterruptedException | IllegalStateException
                | NumberFormatException exception) {
            LOGGER.warn("Could not retrieve length of log file {} in folder {}: {}", //
                    this.logNamePattern, this.logPath, exception.getMessage());
            return 0;
        }
    }

    private String runWcCommand() throws UnsupportedOperationException, IOException, InterruptedException {
        final Container.ExecResult result = this.container.execInContainer("find", this.logPath, //
                "-name", this.logNamePattern, //
                "-exec", "wc", "-l", "{}", "+");
        return result.getStdout();
    }

    private long parseWcOutput(final String stdout) throws IOException {
        long result = 0;
        try (final BufferedReader reader = new BufferedReader(new StringReader(stdout))) {
            String line;
            while ((line = reader.readLine()) != null) {
                result = processLine(result, line);
            }
        }
        return result;
    }

    private boolean isValid(final long result) {
        return result > 0;
    }

    private long processLine(final long soFar, final String line) {
        if (line.isBlank()) {
            return soFar;
        }
        final Matcher matcher = LINE_COUNT.matcher(line);
        if (!matcher.matches()) {
            return soFar;
        }
        if (isValid(soFar)) {
            throw new IllegalStateException(MessageFormat.format( //
                    "Found multiple files in folder {0} matching pattern {1}", //
                    this.logPath, this.logNamePattern));
        }
        return Long.parseLong(matcher.group(1));
    }
}
