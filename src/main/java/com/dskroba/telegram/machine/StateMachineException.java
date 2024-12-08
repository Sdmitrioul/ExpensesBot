package com.dskroba.telegram.machine;

import com.dskroba.base.exception.CustomException;

public class StateMachineException extends CustomException {
    public StateMachineException(String message) {
        super(message);
    }
}
