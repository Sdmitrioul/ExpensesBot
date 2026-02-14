package com.dskroba.statemachine.state;

import com.dskroba.statemachine.effects.Effect;
import com.dskroba.statemachine.state.descriptors.StateDescriptor;

public record StateRecord(StateDescriptor descriptor, Effect initialEffect, boolean isCancellable) implements State {
    public static StateRecord cancelable(StateDescriptor descriptor, Effect initialEffect) {
        return new StateRecord(descriptor, initialEffect, true);
    }

    public static StateRecord requiring(StateDescriptor descriptor, Effect initialEffect) {
        return new StateRecord(descriptor, initialEffect, false);
    }
}
