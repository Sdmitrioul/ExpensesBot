package com.dskroba.telegram.machine;

public final class TryAgainAction implements Action {
    private final String message;

    public TryAgainAction(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public State execute(State previousState) {
        return previousState;
    }

    @Override
    public String getDescription() {
        return "Try again";
    }
}
