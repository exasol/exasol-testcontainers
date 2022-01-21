package com.exasol.containers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.containers.ContainerLaunchException;

@Tag("fast")
class DBVersionCheckerTest {

    @Test
    private void assertBadParse1() {
        String dbVersionStr = "aa.bb.cc";
        final ContainerLaunchException exception = assertThrows(ContainerLaunchException.class, ()-> {DBVersionChecker.minSupportedDbVersionCheck(dbVersionStr);});
        assertThat(exception.getMessage(), containsString("E-ETC-15"));
    }
    @Test
    private void assertBadParse2() {
        String dbVersionStr = "10.1.144.12";
        final ContainerLaunchException exception = assertThrows(ContainerLaunchException.class, ()-> {DBVersionChecker.minSupportedDbVersionCheck(dbVersionStr);});
        assertThat(exception.getMessage(), containsString("E-ETC-14"));
    }
    @Test
    private void assertBadParse3() {
        String dbVersionStr = "10.1";
        final ContainerLaunchException exception = assertThrows(ContainerLaunchException.class, ()-> {DBVersionChecker.minSupportedDbVersionCheck(dbVersionStr);});
        assertThat(exception.getMessage(), containsString("E-ETC-14"));
    }
    @Test
    private void assertThrowsInvalidVersion1() {
        String dbVersionStr = "5.9.13";
        final ContainerLaunchException exception = assertThrows(ContainerLaunchException.class, ()-> {DBVersionChecker.minSupportedDbVersionCheck(dbVersionStr);});
        assertThat(exception.getMessage(), containsString("E-ETC-13"));
    }
    @Test
    private void assertThrowsInvalidVersion2() {
        String dbVersionStr = "6.2.13";
        final ContainerLaunchException exception = assertThrows(ContainerLaunchException.class, ()-> {DBVersionChecker.minSupportedDbVersionCheck(dbVersionStr);});
        assertThat(exception.getMessage(), containsString("E-ETC-13"));
    }
    @Test
    private void assertThrowsInvalidVersion3() {
        String dbVersionStr = "5.1.0";
        final ContainerLaunchException exception = assertThrows(ContainerLaunchException.class, ()-> {DBVersionChecker.minSupportedDbVersionCheck(dbVersionStr);});
        assertThat(exception.getMessage(), containsString("E-ETC-13"));
    }
    @Test
    private void assertDoesntThrowValidVersion1() {
        String dbVersionStr = "6.3.0";
        DBVersionChecker.minSupportedDbVersionCheck(dbVersionStr);
    }
    @Test
    private void assertDoesntThrowValidVersion2() {
        String dbVersionStr = "7.2.4";
        DBVersionChecker.minSupportedDbVersionCheck(dbVersionStr);
    }

}