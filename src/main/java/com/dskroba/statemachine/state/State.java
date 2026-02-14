package com.dskroba.statemachine.state;

import com.dskroba.statemachine.effects.Effect;
import com.dskroba.statemachine.state.descriptors.StateDescriptor;

public interface State {
    StateDescriptor descriptor();

    Effect initialEffect();

    default boolean isCancellable() {
        return true;
    }
}
