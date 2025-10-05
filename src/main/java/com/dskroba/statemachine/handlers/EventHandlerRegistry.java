package com.dskroba.statemachine.handlers;

import com.dskroba.statemachine.state.State;

public interface EventHandlerRegistry {
    EventHandler getHandler(State state);
}