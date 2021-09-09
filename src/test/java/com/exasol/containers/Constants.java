package com.exasol.containers;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Constants {

    public static ExitType SUPPORT_INFORMATION_EXIT_TYPE = ExitType.EXIT_ANY;
    public static Path SUPPORT_INFORMATION_PATH = Paths.get("target/support");

    private Constants() {
        // empty by intention
    }
}
