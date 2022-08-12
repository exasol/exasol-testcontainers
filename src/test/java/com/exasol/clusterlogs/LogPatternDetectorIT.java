package com.exasol.clusterlogs;

import static com.exasol.containers.ExasolContainerConstants.BUCKETFS_DAEMON_LOG_FILENAME_PATTERN;
import static com.exasol.containers.ExasolContainerConstants.EXASOL_CORE_DAEMON_LOGS_PATH;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

import java.io.IOException;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.exasol.containers.ExasolContainer;
import com.exasol.containers.ExasolService;

@Tag("slow")
@Testcontainers
class LogPatternDetectorIT {

    @Container
    private static final ExasolContainer<? extends ExasolContainer<?>> container = new ExasolContainer<>()
            .withRequiredServices(ExasolService.BUCKETFS) //
            .withReuse(true);

    @Test
    void testGetActualLog() {
        final LogPatternDetector detector = createPatternDetector("dummypattern");
        assertThat(detector.getActualLog(), containsString("UNPACK THREAD started"));
    }

    @Test
    void testIsPatternPresentFindsPattern() throws IOException, InterruptedException {
        final LogPatternDetector detector = createPatternDetector("UNPACK THREAD started");
        assertThat(detector.isPatternPresent(), is(true));
    }

    @Test
    void testIsPatternPresentDoesNotPattern() throws IOException, InterruptedException {
        final LogPatternDetector detector = createPatternDetector("dummypattern");
        assertThat(detector.isPatternPresent(), is(false));
    }

    private LogPatternDetector createPatternDetector(final String pattern) {
        return new LogPatternDetectorFactory(container).createLogPatternDetector(EXASOL_CORE_DAEMON_LOGS_PATH,
                BUCKETFS_DAEMON_LOG_FILENAME_PATTERN, pattern);
    }
}
