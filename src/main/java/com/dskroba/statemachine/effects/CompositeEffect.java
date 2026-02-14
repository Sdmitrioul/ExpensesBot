package com.dskroba.statemachine.effects;

import java.util.LinkedList;
import java.util.List;

public final class CompositeEffect implements Effect {
    private final List<Effect> effects = new LinkedList<>();

    <T> CompositeEffect(Effect first, Effect second) {
        effects.add(first);
        this.composite(second);
    }

    @Override
    public CompositeEffect composite(Effect effect) {
        if (effect instanceof CompositeEffect compositeEffect) {
            this.effects.addAll(compositeEffect.payload());
            return this;
        }
        this.effects.add(effect);
        return this;
    }

    public List<Effect> payload() {
        return effects;
    }
}
