package com.dskroba.base.limiter;

public interface RateLimiter {
    boolean tryAcquire();

    void acquire();
}
