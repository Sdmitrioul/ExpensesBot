package com.dskroba.statemachine.effects;

public sealed interface Effect<T> permits FileEffect, MessageEffect, SelectEffect {
    T payload();
}
