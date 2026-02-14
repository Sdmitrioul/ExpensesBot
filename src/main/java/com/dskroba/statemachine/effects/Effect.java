package com.dskroba.statemachine.effects;

public sealed interface Effect permits FileEffect, MessageEffect, SelectEffect, CompositeEffect {
    default CompositeEffect composite(Effect effect) {
        return new CompositeEffect(this, effect);
    }
}
