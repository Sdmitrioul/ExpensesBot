package com.dskroba.statemachine.state;

import com.dskroba.statemachine.effects.Effect;
import com.dskroba.statemachine.effects.SelectEffect;

import java.util.List;

public enum GroupStates implements State {
    ACTIVE_GROUPS_STATES(null),
    CREATE_GROUP_STATE(SelectEffect.withCancelOption("Please provide group name:", List.of())),
    ;

    private final Effect<?> effect;

    GroupStates(Effect<?> effect) {
        this.effect = effect;
    }

    @Override
    public Effect<?> initialEffect() {
        return null;
    }
}
