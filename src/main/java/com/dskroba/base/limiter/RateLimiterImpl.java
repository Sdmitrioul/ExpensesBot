package com.dskroba.base.limiter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Clock;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class RateLimiterImpl implements RateLimiter {
    private static final Logger LOGGER = LogManager.getLogger(RateLimiterImpl.class);
    private final AtomicReference<Counter> counterReference = new AtomicReference<>();
    private final Clock clock;
    private final Duration duration;
    private final int threshold;
    private final int retries;

    public RateLimiterImpl(Clock clock, Duration duration, int threshold, int retries) {
        this.clock = clock;
        this.threshold = threshold;
        this.duration = duration;
        this.retries = retries;
        counterReference.set(new Counter(clock.millis()));
    }

    @Override
    public boolean tryAcquire() {
        tryUpdate();
        return counterReference.get().counter.getAndIncrement() < threshold;
    }

    @Override
    public void acquire() {
        try {
            for (int i = 0; i < retries; i++) {
                if (tryAcquire()) {
                    return;
                }
                Thread.sleep(duration.toMillis() / 2);
            }
            throw new RateLimiterException("Unable to acquire rate limiter.");
        } catch (InterruptedException e) {
            LOGGER.error("Interrupted while waiting for counter.", e);
            throw new RuntimeException(e);
        }
    }

    private void tryUpdate() {
        Counter counter = counterReference.get();
        long currentTime = clock.millis();
        if (counter.time + duration.toMillis() <= currentTime) {
            counterReference.compareAndSet(counter, new Counter(currentTime));
        }
    }

    private static final class Counter {
        public final AtomicInteger counter = new AtomicInteger(0);
        private final long time;


        private Counter(long time) {
            this.time = time;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Counter counter1 = (Counter) o;
            return time == counter1.time;
        }

        @Override
        public int hashCode() {
            return Objects.hash(time);
        }
    }
}
