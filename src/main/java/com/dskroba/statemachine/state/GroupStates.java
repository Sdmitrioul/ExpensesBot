package com.dskroba.statemachine.state;

import com.dskroba.statemachine.effects.Effect;
import com.dskroba.statemachine.effects.SelectEffect;

import java.util.List;

public enum GroupStates implements State {
    ACTIVATE_GROUP_STATE(null),
    CREATE_GROUP_STATE(SelectEffect.withCancelOption("Please provide group name:", List.of())),
    UPDATE_GROUP_STATE(null),
    SELECT_UPDATE_ACTION_STATE(null),
    UPDATE_ACTION_CONSUMER_STATE(null);

    public static final String GROUP_ATTRIBUTE_NAME = "$group";

    private final Effect<?> effect;

    GroupStates(Effect<?> effect) {
        this.effect = effect;
    }

    @Override
    public Effect<?> initialEffect() {
        return null;
    }
}
