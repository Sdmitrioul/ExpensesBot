package com.dskroba.base.limiter;

import com.dskroba.base.exception.CustomException;

public class RateLimiterException extends CustomException {
    public RateLimiterException(String message) {
        super(message);
    }
}
