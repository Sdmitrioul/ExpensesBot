package com.dskroba.statemachine.handlers;

import com.dskroba.statemachine.effects.Effect;
import com.dskroba.statemachine.events.Event;
import com.dskroba.statemachine.state.State;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class StateSelector extends AbstractHandler {
    private final Map<String, State> eventToStateMap;

    public StateSelector(Map<String, State> eventToStateMap) {
        this.eventToStateMap = eventToStateMap;
    }

    @Override
    protected Optional<List<Effect<?>>> handleStateEvents(Event<?> event) {
        return handleMessageEvent(event)
                .map(String::toLowerCase)
                .map(eventToStateMap::get)
                .map(this::handleChangeOfState);
    }
}
