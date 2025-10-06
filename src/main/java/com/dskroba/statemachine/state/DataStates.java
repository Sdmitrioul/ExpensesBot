package com.dskroba.statemachine.state;

import com.dskroba.statemachine.effects.Effect;
import com.dskroba.statemachine.effects.SelectEffect;
import com.dskroba.type.SelectOption;

import java.util.List;

public enum DataStates implements State {
    CHOOSE_OPERATION(SelectEffect.withCancelOption("Choose command", List.of(
            SelectOption.base("Import"),
            SelectOption.base("Export")))),
    CHOOSE_OUTPUT_FORMAT(SelectEffect.withCancelOption("Select output file format", List.of(
            SelectOption.base("Csv"),
            SelectOption.base("Xlsx"))));

    public static final String TIMEFRAME_ATTRIBUTE_NAME = "$timeframe_filter";

    private final Effect<?> effect;

    DataStates(Effect<?> effect) {
        this.effect = effect;
    }

    @Override
    public Effect<?> initialEffect() {
        return effect;
    }

}
