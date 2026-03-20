package com.dskroba.statemachine.handlers.google;

import com.dskroba.google.GoogleTable;
import com.dskroba.statemachine.effects.Effect;
import com.dskroba.statemachine.effects.MessageEffect;
import com.dskroba.statemachine.effects.SelectEffect;
import com.dskroba.statemachine.events.Event;
import com.dskroba.statemachine.handlers.AbstractHandlerWithFactory;
import com.dskroba.statemachine.state.ContextAccessor;
import com.dskroba.statemachine.state.State;
import com.dskroba.statemachine.state.descriptors.StateDescriptor;
import com.dskroba.type.SelectOption;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.Optional;

import static com.dskroba.statemachine.state.descriptors.CommandStatesDescriptors.SELECT_ACTIVE_DOCUMENT;

@Component
public class SelectDocumentHandler extends AbstractHandlerWithFactory {
    @Override
    protected Optional<Effect> handleStateEvents(Event event) {
        return handleMessageEvent(event)
                .map(String::trim)
                .map(this::removeCurrentBraces)
                .map(this::setActiveDocument);
    }

    private String removeCurrentBraces(String document) {
        if (document.startsWith("[ ") && document.endsWith(" ]")) {
            return document.substring(1, document.length() - 1).strip();
        }
        return document;
    }

    private Effect setActiveDocument(String document) {
        var documents = ContextAccessor.principalDocuments();
        for (var doc : documents) {
            if (doc.sheetName().equals(document)) {
                var principal = ContextAccessor.principal()
                        .toBuilder()
                        .activeTableId(doc)
                        .build();
                ContextAccessor.context().setPrincipal(principal);
                return switchToMenu();
            }
        }
        return MessageEffect.of("Unrecognized option")
                .composite(moveToState(SELECT_ACTIVE_DOCUMENT));
    }

    @Override
    public State createState() {
        if (ContextAccessor.principalDocuments().size() == 1) {
            State currentState = ContextAccessor.getState();
            return new State(currentState.descriptor(),
                    MessageEffect.of("You have only one document.")
                            .composite(currentState.initialEffect()),
                    currentState.isCancellable());
        }
        var options = new LinkedList<SelectOption>();
        var documents = ContextAccessor.principalDocuments();
        var activeDocument = ContextAccessor.principalStorage();
        for (GoogleTable doc : documents) {
            if (doc.equals(activeDocument)) {
                options.add(SelectOption.base("[ " + doc.sheetName() + " ]"));
            } else {
                options.add(SelectOption.base(doc.sheetName()));
            }
        }
        return State.cancelable(SELECT_ACTIVE_DOCUMENT,
                SelectEffect.of("Select document you want to use as current storage.", options));
    }

    @Override
    public StateDescriptor stateDescriptor() {
        return SELECT_ACTIVE_DOCUMENT;
    }
}
