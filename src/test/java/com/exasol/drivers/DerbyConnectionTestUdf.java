package com.exasol.drivers;

import com.exasol.ExaIterator;
import com.exasol.ExaMetadata;

import java.sql.DriverManager;

/**
 * This class contains a minimal Java User Defined Functions (UDF).
 * <p>
 *  The purpose of this class is to exercise a database connection from the running Exasol container to another database
 * -- Apache Derby in this case.
 * </p>
 * <p>
 * Derby was chosen, so that the dependencies are minimal in the integration test.
 * </p>
 * <p>
 * Note that this class is <em>intentionally</em> declared package-scoped. When injected into a UDF, this is the scope
 * we need. Also the package will be removed.
 * </p>
 */
class DerbyConnectionTestUdf {
    static String run(ExaMetadata metadata, ExaIterator context) throws Exception {
        assertDerbyDriverAvailable();
        try(java.sql.Connection connection = DriverManager.getConnection("jdbc:derby:memory:test;create=true;")
        ) {
            return connection.getMetaData().getDatabaseProductName();
        }
    }

    private static void assertDerbyDriverAvailable() {
        if(DriverManager.drivers().noneMatch(driver -> driver.getClass().getName().toLowerCase().contains("derby"))) {
            throw new AssertionError("Derby driver not available through driver manager.");
        }
    }
}
