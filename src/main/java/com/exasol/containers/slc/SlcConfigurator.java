package com.exasol.containers.slc;

import java.sql.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.exasol.containers.UncheckedSqlException;
import com.exasol.errorreporting.ExaError;

/**
 * This class is responsible for reading and writing the Script Language Container (SLC) configuration from and to an
 * Exasol database.
 */
class SlcConfigurator {
    private static final Logger LOGGER = LoggerFactory.getLogger(SlcConfigurator.class);
    private final Connection connection;

    SlcConfigurator(final Connection connection) {
        this.connection = connection;
    }

    /**
     * Read the SYSTEM value for parameter SCRIPT_LANGUAGES from the Exasol database.
     * 
     * @return the SLC configuration
     */
    SlcConfiguration read() {
        return SlcConfiguration.parse(getScriptLanguagesSystemValue());
    }

    /**
     * Write the given SLC configuration to the Exasol database using {@code ALTER SYSTEM} and {@code ALTER SESSION}
     * commands.
     * 
     * @param config the configuration to write
     */
    @SuppressWarnings("java:S2077") // ALTER SESSION and ALTER SYSTEM don't support prepared statements
    void write(final SlcConfiguration config) {
        final String value = escapeQuotes(config.format());
        LOGGER.debug("Writing SLC configuration to database: {}", value);
        try (Statement statement = connection.createStatement()) {
            statement.execute("ALTER SYSTEM SET SCRIPT_LANGUAGES='" + value + "'");
            statement.execute("ALTER SESSION SET SCRIPT_LANGUAGES='" + value + "'");
        } catch (final SQLException exception) {
            throw new UncheckedSqlException(ExaError.messageBuilder("E-ETC-32")
                    .message("Failed to write SLC configuration to the database").toString(), exception);
        }
    }

    private String escapeQuotes(final String config) {
        return config.replace("'", "''");
    }

    private String getScriptLanguagesSystemValue() {
        final String query = "SELECT system_value FROM exa_parameters WHERE parameter_name='SCRIPT_LANGUAGES'";
        try (final PreparedStatement stmt = connection.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                return rs.getString(1);
            }
            throw new IllegalStateException("No SLC configuration found.");
        } catch (final SQLException exception) {
            throw new UncheckedSqlException(
                    ExaError.messageBuilder("E-ETC-33")
                            .message("Failed to read SLC configuration using query {{query}}", query).toString(),
                    exception);
        }
    }
}
