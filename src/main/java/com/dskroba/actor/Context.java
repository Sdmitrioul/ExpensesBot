package com.dskroba.actor;

import com.dskroba.base.Listener;
import com.dskroba.principals.Principal;
import com.dskroba.statemachine.state.State;
import com.dskroba.statemachine.state.StateContext;

import java.util.function.Consumer;

public final class Context {
    public static final ScopedValue<Context> PROVIDER = ScopedValue.newInstance();

    private volatile Principal principal;
    private final StateContext stateContext;
    private final Listener<Principal> principalChangeListener;
    private final Listener<StateContext> stateContextListener;

    public Context(Principal principal, StateContext stateContext,
                   Listener<Principal> principalChangeListener,
                   Listener<StateContext> stateContextListener) {
        this.principal = principal;
        this.stateContext = stateContext;
        this.principalChangeListener = principalChangeListener;
        this.stateContextListener = stateContextListener;
    }

    public Principal getPrincipal() {
        return principal;
    }

    public void setPrincipal(Principal principal) {
        this.principal = principal;
        this.principalChangeListener.receive(principal);
    }

    public State getState() {
        return stateContext.getState();
    }


    public <T> T getAttribute(String key, Class<T> type) {
        return stateContext.getAttribute(key, type);
    }

    public void updateStateContext(Consumer<StateContext> updater) {
        updater.accept(stateContext);
        var cloned = stateContext.cloneInstance();
        this.stateContextListener.receive(cloned);
    }
}
