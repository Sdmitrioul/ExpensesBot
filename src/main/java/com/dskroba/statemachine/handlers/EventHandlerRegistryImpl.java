package com.dskroba.statemachine.handlers;

import com.dskroba.statemachine.state.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class EventHandlerRegistryImpl implements EventHandlerRegistry {
    private final Map<State, EventHandler> handlers;
    private final DefaultEventHandler defaultHandler;

    @Autowired
    public EventHandlerRegistryImpl(DefaultEventHandler defaultHandler, EventHandler... handlers) {
        Map<State, EventHandler> eventHandlerMap = new HashMap<>();

        for (EventHandler handler : handlers) {
            if (handler.state() != null) {
                eventHandlerMap.put(handler.state(), handler);
            }
        }

        this.handlers = Map.copyOf(eventHandlerMap);
        this.defaultHandler = defaultHandler;
    }

    @Override
    public EventHandler getHandler(State state) {
        if (state == null) {
            return defaultHandler;
        }
        return handlers.getOrDefault(state, defaultHandler);
    }
}
