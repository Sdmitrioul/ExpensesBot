package com.dskroba.statemachine.handlers;

import com.dskroba.actor.StateContext;
import com.dskroba.statemachine.effects.Effect;
import com.dskroba.statemachine.effects.MessageEffect;
import com.dskroba.statemachine.events.Event;
import com.dskroba.statemachine.state.CommandStates;
import com.dskroba.statemachine.state.State;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class DefaultEventHandler extends AbstractHandler {
    @Override
    public State state() {
        return null;
    }

    @Override
    protected Optional<List<Effect<?>>> handleStateEvents(Event<?> event) {
        log.warn("Unhandled state: {}", StateContext.PROVIDER.get().getState());
        var effects = new ArrayList<Effect<?>>();
        effects.add(new MessageEffect("""
                Unable to process this request.
                You will be redirect to the main menu."""));
        effects.addAll(handleChangeOfState(CommandStates.MENU));
        return Optional.of(effects);
    }
}