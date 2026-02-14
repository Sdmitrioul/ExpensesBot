package com.dskroba.statemachine.state.descriptors;

public enum DataStatesDescriptors implements StateDescriptor {
    CHOOSE_OPERATION,
    CHOOSE_OUTPUT_FORMAT;

    public static final String TIMEFRAME_ATTRIBUTE_NAME = "$timeframe_filter";
}
