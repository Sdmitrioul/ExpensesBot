package com.dskroba.statemachine.handlers;

import com.dskroba.statemachine.effects.Effect;
import com.dskroba.statemachine.events.Event;
import com.dskroba.statemachine.state.State;

import java.util.List;

public interface EventHandler {
    List<Effect<?>> handleEvent(Event<?> event);

    State state();
}
