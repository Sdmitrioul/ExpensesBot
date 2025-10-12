package com.dskroba.statemachine.state;

import com.dskroba.statemachine.effects.Effect;

public sealed interface State permits CommandStates, DataStates, ExpenseStates, GroupStates, StatisticStates {
    Effect<?> initialEffect();

    default boolean isCancellable() {
        return true;
    }
}
