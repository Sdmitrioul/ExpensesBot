package com.dskroba.statemachine;

import com.dskroba.principals.Principal;
import com.dskroba.statemachine.effects.Effect;
import com.dskroba.statemachine.events.Event;
import com.dskroba.telegram.TelegramId;

import java.util.List;

public interface IOModule<I> {
    Event<?> parse(I input, Principal issuer);

    TelegramId provideIssuer(I input);

    void handleEffects(List<Effect<?>> effects);
}
