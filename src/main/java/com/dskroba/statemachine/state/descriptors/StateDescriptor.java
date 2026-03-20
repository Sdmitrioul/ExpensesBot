package com.dskroba.statemachine.state.descriptors;

public sealed interface StateDescriptor permits CommandStatesDescriptors, DataStatesDescriptors, ExpensesStatesDescriptors, GoogleStatesDescriptors, StatisticStatesDescriptors {
    String name();
}
