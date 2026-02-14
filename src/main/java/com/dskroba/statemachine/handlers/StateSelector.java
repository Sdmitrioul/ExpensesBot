package com.dskroba.statemachine.handlers;

import com.dskroba.statemachine.effects.Effect;
import com.dskroba.statemachine.events.Event;
import com.dskroba.statemachine.state.descriptors.StateDescriptor;

import java.util.Map;
import java.util.Optional;

public abstract class StateSelector extends AbstractHandlerWithFactory {
    private final Map<String, StateDescriptor> eventToStateMap;

    public StateSelector(Map<String, StateDescriptor> eventToStateMap) {
        this.eventToStateMap = eventToStateMap;
    }

    @Override
    protected Optional<Effect> handleStateEvents(Event event) {
        return handleMessageEvent(event)
                .map(String::toLowerCase)
                .map(eventToStateMap::get)
                .map(this::moveToState);
    }
}
