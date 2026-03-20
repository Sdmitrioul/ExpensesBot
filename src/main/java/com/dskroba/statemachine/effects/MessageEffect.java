package com.dskroba.statemachine.effects;

public record MessageEffect(String payload) implements Effect {
    public static MessageEffect of(String payload) {
        return new MessageEffect(payload);
    }
}
