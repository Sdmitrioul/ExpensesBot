package com.dskroba.statemachine.handlers.google;

import com.dskroba.actor.PrincipalContext;
import com.dskroba.actor.StateContext;
import com.dskroba.statemachine.effects.Effect;
import com.dskroba.statemachine.effects.MessageEffect;
import com.dskroba.statemachine.events.Event;
import com.dskroba.statemachine.handlers.AbstractHandlerWithFactory;
import com.dskroba.statemachine.state.State;
import com.dskroba.statemachine.state.StateRecord;
import com.dskroba.statemachine.state.descriptors.StateDescriptor;

import java.util.Optional;

import static com.dskroba.statemachine.state.descriptors.CommandStatesDescriptors.SELECT_ACTIVE_DOCUMENT;

public class SelectDocumentHandler extends AbstractHandlerWithFactory {
    @Override
    protected Optional<Effect> handleStateEvents(Event event) {
        return Optional.empty();
    }

    @Override
    public State createState() {
        if (PrincipalContext.getAllTables().size() == 1) {
            State currentState = StateContext.getCurrentState();
            return new StateRecord(currentState.descriptor(), 
                    new MessageEffect("You have only one document.").composite(currentState.initialEffect()), currentState.isCancellable());
        }
        return null;
    }

    @Override
    public StateDescriptor stateDescriptor() {
        return SELECT_ACTIVE_DOCUMENT;
    }
}
