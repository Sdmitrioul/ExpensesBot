package com.dskroba.telegram.machine;

import com.dskroba.telegram.UserContext;
import com.dskroba.telegram.machine.state.StateFactory;

public final class CancelAction implements Action {
    public static final String NAME = "Cancel";
    private final State toState;

    public static Action create(UserContext context) {
        return new CancelAction(StateFactory.getStartState(context));
    }

    public CancelAction(State toState) {
        this.toState = toState;
    }

    @Override
    public int priority() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isFullRow() {
        return true;
    }

    @Override
    public State execute(State previous) {
        return toState;
    }

    @Override
    public String getDescription() {
        return NAME;
    }
}
