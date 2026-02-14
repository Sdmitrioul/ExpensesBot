package com.dskroba.statemachine.handlers;

import com.dskroba.actor.StateContext;
import com.dskroba.statemachine.effects.Effect;
import com.dskroba.statemachine.effects.MessageEffect;
import com.dskroba.statemachine.events.Event;
import com.dskroba.statemachine.state.descriptors.StateDescriptor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DefaultEventHandler extends AbstractHandler {
    @Override
    public StateDescriptor stateDescriptor() {
        return null;
    }

    @Override
    protected Optional<Effect> handleStateEvents(Event event) {
        log.warn("Unhandled state: {}", StateContext.getCurrentState());
        return Optional.of(MessageEffect.of("""
                Unable to process this request.
                You will be redirect to the main menu.""").composite(switchToMenu()));
    }
}