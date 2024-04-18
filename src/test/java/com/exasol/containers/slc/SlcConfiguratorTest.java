package com.exasol.containers.slc;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exasol.containers.UncheckedSqlException;

@ExtendWith(MockitoExtension.class)
class SlcConfiguratorTest {

    @Mock
    Connection connectionMock;
    @Mock
    Statement statementMock;

    @BeforeEach
    void configureMock() throws SQLException {
        when(connectionMock.createStatement()).thenReturn(statementMock);
    }

    @Test
    void rethrowsException() throws SQLException {
        final SlcConfiguration config = SlcConfiguration.parse("a=b");
        final SlcConfigurator testee = testee();
        when(connectionMock.createStatement()).thenThrow(new SQLException("expected"));
        final UncheckedSqlException exception = assertThrows(UncheckedSqlException.class, () -> testee.write(config));
        assertThat(exception.getMessage(), equalTo("E-ETC-32: Failed to write SLC configuration to the database"));
    }

    @Test
    void writeExecutesStatements() throws SQLException {
        testee().write(SlcConfiguration.parse("a=b"));
        verify(statementMock).execute("ALTER SYSTEM SET SCRIPT_LANGUAGES='a=b'");
        verify(statementMock).execute("ALTER SESSION SET SCRIPT_LANGUAGES='a=b'");
    }

    @Test
    void writeExecutesStatementsEscapesQuote() throws SQLException {
        testee().write(SlcConfiguration.parse("a='b'"));
        verify(statementMock).execute("ALTER SYSTEM SET SCRIPT_LANGUAGES='a=''b'''");
        verify(statementMock).execute("ALTER SESSION SET SCRIPT_LANGUAGES='a=''b'''");
    }

    SlcConfigurator testee() {
        return new SlcConfigurator(connectionMock);
    }
}
