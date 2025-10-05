package com.dskroba.actor;

import com.dskroba.base.exception.CustomException;

public class JobException extends CustomException {
    public JobException(String message) {
        super(message);
    }

    public JobException(Exception e) {
        super(e);
    }
}
