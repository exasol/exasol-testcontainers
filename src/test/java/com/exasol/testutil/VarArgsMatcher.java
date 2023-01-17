package com.exasol.testutil;

import static org.mockito.ArgumentMatchers.any;

/**
 * This class provides a verbose workaround for a breaking change in Mockito 5 reporting stubbing problems when trying
 * to stub a method with variable number of arguments using matcher {@code any()}.
 */
public class VarArgsMatcher {

    private VarArgsMatcher() {
        // only static usage
    }

    /**
     * @return Matcher for any number of strings for a method with variable number of arguments
     */
    public static String[] anyStrings() {
        return any(String[].class);
    }
}
