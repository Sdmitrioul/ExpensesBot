package com.dskroba.base;

public interface RateLimiter {
    void waitUntilCanAcquire() throws InterruptedException;
    void acquire();
    boolean canAcquireNow();
}
