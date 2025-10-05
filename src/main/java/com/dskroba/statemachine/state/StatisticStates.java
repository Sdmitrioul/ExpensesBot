package com.dskroba.statemachine.state;

import com.dskroba.statemachine.effects.Effect;
import com.dskroba.statemachine.effects.SelectEffect;
import com.dskroba.type.SelectOption;

import java.util.List;

public enum StatisticStates implements State {
    STATISTIC_STATE(SelectEffect.of(List.of(
            SelectOption.base("Day", 30),
            SelectOption.base("Week", 29),
            SelectOption.base("Month", 28),
            SelectOption.fullRowOption("Compare months", 27),
            SelectOption.CANCEL_OPTION)));
    private final Effect<?> effect;

    StatisticStates(Effect<?> effect) {
        this.effect = effect;
    }

    @Override
    public Effect<?> initialEffect() {
        return effect;
    }
}
