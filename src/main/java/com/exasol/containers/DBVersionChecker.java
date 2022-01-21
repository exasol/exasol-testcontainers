package com.exasol.containers;

import java.util.regex.Pattern;

import com.exasol.errorreporting.ExaError;

import org.testcontainers.containers.ContainerLaunchException;

public class DBVersionChecker {
    private static final int MAJOR_DEPRECATED_VERSION = 6;
    private static final int MINOR_DEPRECATED_VERSION = 2;

    public static void minSupportedDbVersionCheck(String dbVersion) {
        String[] dbVersionSplit = dbVersion.split(Pattern.quote("."));
        if (dbVersionSplit.length != 3) {
            throw new ContainerLaunchException(
                    ExaError.messageBuilder("E-ETC-14").message("Failed to parse database version. Version tag is invalid.").ticketMitigation().toString());
        }
        int majorVersion;
        int minorVersion;
        try {
            majorVersion = Integer.parseInt(dbVersionSplit[0]);
            minorVersion = Integer.parseInt(dbVersionSplit[1]);
        } catch (Exception e) {
            throw new ContainerLaunchException(
                    ExaError.messageBuilder("E-ETC-15").message("Failed to parse database version. Version tag is invalid.").ticketMitigation().toString());
        }
        if ((majorVersion == MAJOR_DEPRECATED_VERSION && minorVersion <= MINOR_DEPRECATED_VERSION)
                || (majorVersion < MAJOR_DEPRECATED_VERSION)) {
            throwDBVersionNotSupportedException();
        }
    }

    private static void throwDBVersionNotSupportedException() {
        throw new ContainerLaunchException(
                ExaError.messageBuilder("E-ETC-13")
                        .message("Exasol Database version " + MAJOR_DEPRECATED_VERSION + "." + MINOR_DEPRECATED_VERSION
                                + " and lower are no longer supported in this version of Exasol Testcontainers.")
                        .toString());
    }
}
