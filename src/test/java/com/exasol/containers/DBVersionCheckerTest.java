package com.exasol.containers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.testcontainers.containers.ContainerLaunchException;

@Tag("fast")
class DBVersionCheckerTest {

    @Test
    void testAssertBadParse1() {
        String dbVersionStr = "aa.bb.cc";
        final ContainerLaunchException exception = assertThrows(ContainerLaunchException.class, ()-> {DBVersionChecker.minSupportedDbVersionCheck(dbVersionStr);});
        assertThat(exception.getMessage(), containsString("E-ETC-15"));
    }
    @Test
    void testAssertBadParse2() {
        String dbVersionStr = "10.1.144.12";
        final ContainerLaunchException exception = assertThrows(ContainerLaunchException.class, ()-> {DBVersionChecker.minSupportedDbVersionCheck(dbVersionStr);});
        assertThat(exception.getMessage(), containsString("E-ETC-14"));
    }
    @Test
    void testAssertBadParse3() {
        String dbVersionStr = "10.1";
        final ContainerLaunchException exception = assertThrows(ContainerLaunchException.class, ()-> {DBVersionChecker.minSupportedDbVersionCheck(dbVersionStr);});
        assertThat(exception.getMessage(), containsString("E-ETC-14"));
    }
    @Test
    void testAssertThrowsInvalidVersion1() {
        String dbVersionStr = "5.9.13";
        final ContainerLaunchException exception = assertThrows(ContainerLaunchException.class, ()-> {DBVersionChecker.minSupportedDbVersionCheck(dbVersionStr);});
        assertThat(exception.getMessage(), containsString("E-ETC-13"));
    }
    @Test
    void testAssertThrowsInvalidVersion2() {
        String dbVersionStr = "6.2.13";
        final ContainerLaunchException exception = assertThrows(ContainerLaunchException.class, ()-> {DBVersionChecker.minSupportedDbVersionCheck(dbVersionStr);});
        assertThat(exception.getMessage(), containsString("E-ETC-13"));
    }
    @Test
    void testAssertThrowsInvalidVersion3() {
        String dbVersionStr = "5.1.0";
        final ContainerLaunchException exception = assertThrows(ContainerLaunchException.class, ()-> {DBVersionChecker.minSupportedDbVersionCheck(dbVersionStr);});
        assertThat(exception.getMessage(), containsString("E-ETC-13"));
    }
    @Test
    void testAssertDoesntThrowValidVersion1() {
        String dbVersionStr = "6.3.0";
        DBVersionChecker.minSupportedDbVersionCheck(dbVersionStr);
    }
    @Test
    void testAssertDoesntThrowValidVersion2() {
        String dbVersionStr = "7.2.4";
        DBVersionChecker.minSupportedDbVersionCheck(dbVersionStr);
    }

}