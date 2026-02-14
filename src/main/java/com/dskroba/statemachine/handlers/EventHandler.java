package com.dskroba.statemachine.handlers;

import com.dskroba.statemachine.effects.Effect;
import com.dskroba.statemachine.events.Event;
import com.dskroba.statemachine.state.descriptors.StateDescriptor;

public interface EventHandler {
    Effect handleEvent(Event event);

    StateDescriptor stateDescriptor();
}
