package com.dskroba.statemachine.state;

import com.dskroba.statemachine.state.descriptors.StateDescriptor;

public interface StateFactory {
    State createState();

    StateDescriptor stateDescriptor();
}
