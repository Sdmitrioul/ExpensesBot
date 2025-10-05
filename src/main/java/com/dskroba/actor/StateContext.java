package com.dskroba.actor;

import com.dskroba.statemachine.state.State;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public final class StateContext {
    public static final ScopedValue<StateContext> PROVIDER = ScopedValue.newInstance();

    private volatile State state;
    private final ConcurrentHashMap<String, Object> attributes = new ConcurrentHashMap<>();

    public StateContext(State state) {
        this.state = state;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public <T> T getAttribute(String key, Class<T> type) {
        return type.cast(attributes.get(key));
    }

    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    public boolean attributeExists() {
        return !attributes.isEmpty();
    }

    public void clearAttributes() {
        attributes.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof StateContext that)) return false;
        return state == that.state;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(state);
    }

    @Override
    public String toString() {
        return "StateContext{" +
                "state=" + state +
                ", attributes=" + attributes +
                '}';
    }
}
