package com.dskroba.statemachine.handlers;

import com.dskroba.statemachine.state.State;
import com.dskroba.statemachine.state.StateFactory;
import com.dskroba.statemachine.state.descriptors.StateDescriptor;

public interface EventHandlerRegistry {
    EventHandler getHandler(State state);

    StateFactory getFactory(StateDescriptor descriptor);
}