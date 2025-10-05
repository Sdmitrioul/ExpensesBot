package com.dskroba.statemachine.effects;

public record MessageEffect(String payload) implements Effect<String> {
}
