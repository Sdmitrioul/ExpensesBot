package com.dskroba.statemachine.handlers.google;

import com.dskroba.actor.PrincipalContext;
import com.dskroba.actor.StateContext;
import com.dskroba.google.GoogleTable;
import com.dskroba.statemachine.effects.Effect;
import com.dskroba.statemachine.effects.MessageEffect;
import com.dskroba.statemachine.events.Event;
import com.dskroba.statemachine.handlers.AbstractHandlerWithFactory;
import com.dskroba.statemachine.state.State;
import com.dskroba.statemachine.state.StateRecord;
import com.dskroba.statemachine.state.descriptors.StateDescriptor;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

import static com.dskroba.statemachine.state.descriptors.GoogleStatesDescriptors.*;

@Component
public class SheetNameHandler extends AbstractHandlerWithFactory {
    @Override
    protected Optional<Effect> handleStateEvents(Event event) {
        return handleMessageEvent(event)
                .flatMap(name ->
                        handleDuplicationName(name)
                                .or(() -> handlePutInCash(name)));
    }

    private Optional<Effect> handlePutInCash(String name) {
        StateContext.getContext().setAttribute(SHEET_NAME_ATTRIBUTE, name);
        return Optional.of(moveToState(SAVE_GOOGLE_SHEET));
    }

    private Optional<Effect> handleDuplicationName(String name) {
        if (PrincipalContext.getAllTables()
                .stream()
                .map(GoogleTable::sheetName)
                .anyMatch(sheetName -> Objects.equals(sheetName, name))) {
            return Optional.of(MessageEffect.of("Please, use other name, this name is already in use."));
        }
        return Optional.empty();
    }

    @Override
    public State createState() {
        return new StateRecord(PROVIDE_SHEET_NAME,
                new MessageEffect("Please, provide name for sheet to use."),
                PrincipalContext.isInitiated());
    }

    @Override
    public StateDescriptor stateDescriptor() {
        return PROVIDE_SHEET_NAME;
    }
}
