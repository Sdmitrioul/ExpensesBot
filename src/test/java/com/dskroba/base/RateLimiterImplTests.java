package com.dskroba.base;

import com.dskroba.base.limiter.RateLimiterException;
import com.dskroba.base.limiter.RateLimiterImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class RateLimiterImplTests {
    private Clock fixedClock;
    private RateLimiterImpl rateLimiter;

    @BeforeEach
    void setUp() {
        fixedClock = Clock.systemDefaultZone();
        rateLimiter = new RateLimiterImpl(fixedClock, Duration.ofSeconds(1), 5, 3);
    }

    @Test
    void testInitialization() {
        assertNotNull(rateLimiter, "Rate limiter should be initialized");
    }

    @Test
    void testTryAcquireWithinLimit() {
        for (int i = 0; i < 5; i++) {
            assertTrue(rateLimiter.tryAcquire(), "tryAcquire should succeed within the limit");
        }
    }

    @Test
    void testTryAcquireExceedingLimit() {
        for (int i = 0; i < 5; i++) {
            assertTrue(rateLimiter.tryAcquire(), "tryAcquire should succeed within the limit");
        }
        assertFalse(rateLimiter.tryAcquire(), "tryAcquire should fail after reaching the limit");
    }

    @Test
    void testCounterResetAfterDuration() throws InterruptedException {
        for (int i = 0; i < 5; i++) {
            assertTrue(rateLimiter.tryAcquire(), "tryAcquire should succeed within the limit");
        }
        assertFalse(rateLimiter.tryAcquire(), "tryAcquire should fail after reaching the limit");

        // Simulate passage of time beyond duration
        Thread.sleep(1000);
        assertTrue(rateLimiter.tryAcquire(), "tryAcquire should succeed after counter reset");
    }

    @Test
    void testAcquireSuccess() {
        assertDoesNotThrow(() -> rateLimiter.acquire(), "acquire should not throw when under the limit");
    }

    @Test
    void testConcurrentAccess() throws InterruptedException {
        Runnable task = () -> assertTrue(rateLimiter.tryAcquire(), "tryAcquire should handle concurrency");
        Thread thread1 = new Thread(task);
        Thread thread2 = new Thread(task);

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();
    }

    @Test
    void testEdgeCaseThresholdOne() {
        RateLimiterImpl rateLimiterEdge = new RateLimiterImpl(fixedClock, Duration.ofSeconds(1), 1, 1);

        assertTrue(rateLimiterEdge.tryAcquire(), "tryAcquire should succeed for threshold of 1");
        assertFalse(rateLimiterEdge.tryAcquire(), "tryAcquire should fail after 1 acquisition for threshold of 1");
    }

    @Test
    void testEdgeCaseZeroRetries() {
        RateLimiterImpl rateLimiterNoRetries = new RateLimiterImpl(fixedClock, Duration.ofSeconds(2), 5, 1);

        for (int i = 0; i < 5; i++) {
            assertDoesNotThrow(() -> rateLimiterNoRetries.acquire(), "acquire should not throw within limit");
        }

        assertThrows(RateLimiterException.class, rateLimiterNoRetries::acquire, "acquire should fail without retries");
    }
}
