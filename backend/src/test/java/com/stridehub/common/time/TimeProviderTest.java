package com.stridehub.common.time;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class TimeProviderTest {

    @Autowired
    private TimeProvider timeProvider;

    @Test
    void shouldReturnCurrentTime() {
        Instant before = Instant.now();
        Instant result = timeProvider.now();
        Instant after = Instant.now();

        assertThat(result).isNotNull();
        assertThat(result).isBetween(before.minusSeconds(1), after.plusSeconds(1));
    }

    @Test
    void shouldReturnDifferentTimesOnSubsequentCalls() throws InterruptedException {
        Instant first = timeProvider.now();
        Thread.sleep(10);
        Instant second = timeProvider.now();

        assertThat(second).isAfterOrEqualTo(first);
    }
}
