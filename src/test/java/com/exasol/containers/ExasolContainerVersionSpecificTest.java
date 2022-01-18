package com.exasol.containers;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.ContainerLaunchException;

/**
 * Test an Exasol Container with version lower than 7.0.
 */
@Tag("fast")
class ExasolContainerVersionSpecificTest {
    private static final ExasolContainer<? extends ExasolContainer<?>> CONTAINER_V6 = new ExasolContainer<>("6.2.7-d1",
            false);
    private static final ExasolContainer<? extends ExasolContainer<?>> CONTAINER_V7 = new ExasolContainer<>("7.0.0",
            false);

//    @Test
//    void testContainer62x(){
//        assertThrows(IllegalArgumentException.class,() -> {ExasolContainer<? extends ExasolContainer<?>> CONTAINER_V6 = new ExasolContainer<>("6.2.7-d1");});
//    }
@Test
void testContainer62x2() {
    ExasolContainer<? extends ExasolContainer<?>> containerV62 = new ExasolContainer<>("6.2.7-d1");
    var exception = assertThrows(IllegalArgumentException.class,
    () -> {
        containerV62.start();

    });
    //containerV62.getRpcUrl();
    assertThat(exception.getCause().getClass() , equalTo(  IllegalArgumentException.class));
}
    @Test
    void testBucketfsPortOnV6() {
        assertThat(CONTAINER_V6.getDefaultInternalBucketfsPort(), equalTo(6583));
    }

    @Test
    void testDatabasePortOnV6() {
        assertThat(CONTAINER_V6.getDefaultInternalDatabasePort(), equalTo(8888));
    }

    @Test
    void testBucketfsPortOnV7() {
        assertThat(CONTAINER_V7.getDefaultInternalBucketfsPort(), equalTo(2580));
    }

    @Test
    void testDatabasePortOnV7() {
        assertThat(CONTAINER_V7.getDefaultInternalDatabasePort(), equalTo(8563));
    }
}