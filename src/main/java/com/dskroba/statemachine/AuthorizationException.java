package com.dskroba.statemachine;

import com.dskroba.base.exception.CustomException;

public class AuthorizationException extends CustomException {
    public AuthorizationException(String message) {
        super(message);
    }
}
