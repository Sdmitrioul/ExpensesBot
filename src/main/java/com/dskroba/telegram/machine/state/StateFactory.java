package com.dskroba.telegram.machine.state;

import com.dskroba.telegram.UserContext;
import com.dskroba.telegram.machine.State;

public final class StateFactory {
    public static State getStartState(UserContext userContext) {
        return new ModuleState(userContext);
    }

    private StateFactory() {
    }
}
