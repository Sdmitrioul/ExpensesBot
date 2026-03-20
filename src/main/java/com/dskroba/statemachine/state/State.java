package com.dskroba.statemachine.state;

import com.dskroba.statemachine.effects.Effect;
import com.dskroba.statemachine.state.descriptors.StateDescriptor;

public record State(StateDescriptor descriptor, Effect initialEffect, boolean isCancellable) {
    public static State cancelable(StateDescriptor descriptor, Effect initialEffect) {
        return new State(descriptor, initialEffect, true);
    }

    public static State requiring(StateDescriptor descriptor, Effect initialEffect) {
        return new State(descriptor, initialEffect, false);
    }
}
