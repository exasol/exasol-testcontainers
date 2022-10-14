package com.exasol.containers.ssh;

import java.time.Duration;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Enables to execute a {@link RunnableWthException} with a number of retries until a configurable timeout is reached.
 *
 * @param <T> class of exceptions that are expected allowing further retries until timeout is reached
 */
public class Retry<T extends Exception> {
    private static final Logger LOGGER = LoggerFactory.getLogger(Retry.class);

    private final Class<T> exceptionClass;
    private final Duration timeout;
    private final Duration interval;
    private Instant start;

    /**
     * @param exceptionClass rate exceptions of this class as failed runs and retry execution until timeout is reached.
     * @param timeout        max. duration until execution must succeed without any exception.
     */
    public Retry(final Class<T> exceptionClass, final Duration timeout) {
        this.exceptionClass = exceptionClass;
        this.timeout = timeout;
        this.interval = Duration.ofSeconds(3);
    }

    /**
     * @param runnable execute this runnable until either timeout or execution succeeds without exception of specified
     *                 class. If the execution throws an exception of a different class then simply forward this
     *                 exception.
     * @throws T type of expected exceptions indicating an execution failure allowing further retries.
     */
    public void retry(final RunnableWthException<T> runnable) throws T {
        this.start = Instant.now();
        for (int i = 0; true; i++) {
            try {
                runnable.run();
                return;
            } catch (final Exception exception) {
                if (!this.exceptionClass.isInstance(exception) || stop()) {
                    throw exception;
                }
                LOGGER.trace("{} - {}. retry after {} seconds", //
                        exception.getMessage(), i + 1, elapsed().plus(this.interval).toSeconds());
            }
            try {
                Thread.sleep(this.interval.toMillis());
            } catch (final InterruptedException interruptedException) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private Duration elapsed() {
        return Duration.between(this.start, Instant.now());
    }

    private boolean stop() {
        return this.timeout.minus(elapsed()).isNegative();
    }

    /**
     * @param <E> class of exceptions that are expected allowing further retries until timeout has reached
     */
    @FunctionalInterface
    public interface RunnableWthException<E extends Exception> {
        /**
         * Execute the runnable
         *
         * @throws E exception that might be thrown if run fails
         */
        void run() throws E;
    }
}
