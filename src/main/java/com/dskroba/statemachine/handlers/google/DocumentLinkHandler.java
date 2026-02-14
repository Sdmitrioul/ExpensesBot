package com.dskroba.statemachine.handlers.google;

import com.dskroba.actor.PrincipalContext;
import com.dskroba.actor.StateContext;
import com.dskroba.base.exception.CustomException;
import com.dskroba.services.GoogleSheetFacade;
import com.dskroba.statemachine.effects.Effect;
import com.dskroba.statemachine.effects.FileEffect;
import com.dskroba.statemachine.effects.MessageEffect;
import com.dskroba.statemachine.events.Event;
import com.dskroba.statemachine.handlers.AbstractHandlerWithFactory;
import com.dskroba.statemachine.state.State;
import com.dskroba.statemachine.state.StateRecord;
import com.dskroba.statemachine.state.descriptors.StateDescriptor;
import com.dskroba.type.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.dskroba.base.Utils.readResourceFile;
import static com.dskroba.statemachine.state.descriptors.CommandStatesDescriptors.PROVIDE_LINK_TO_THE_DOCUMENT;
import static com.dskroba.statemachine.state.descriptors.GoogleStatesDescriptors.PROVIDE_SHEET_NAME;
import static com.dskroba.statemachine.state.descriptors.GoogleStatesDescriptors.SHEET_DOCUMENT_LINK_ATTRIBUTE;

@Component
public class DocumentLinkHandler extends AbstractHandlerWithFactory {
    private static final String SHARE_INSTRUCTIONS_PNG = "share-instructions.png";

    private final GoogleSheetFacade googleSheetFacade;

    @Autowired
    public DocumentLinkHandler(GoogleSheetFacade googleSheetFacade) {
        this.googleSheetFacade = googleSheetFacade;
    }

    @Override
    protected Optional<Effect> handleStateEvents(Event event) {
        return handleMessageEvent(event)
                .flatMap(link -> checkDocumentIsReachable(link)
                        .or(() -> saveLink(link)));
    }

    private Optional<Effect> saveLink(String link) {
        StateContext.getContext().setAttribute(SHEET_DOCUMENT_LINK_ATTRIBUTE, link);
        return Optional.of(moveToState(PROVIDE_SHEET_NAME));
    }

    private Optional<Effect> checkDocumentIsReachable(String link) {
        if (!googleSheetFacade.isReachableDocument(link)) {
            return Optional.of(MessageEffect.of("This link is not supported, please try another one!"));
        }
        return Optional.empty();
    }

    @Override
    public State createState() {
        return new StateRecord(PROVIDE_LINK_TO_THE_DOCUMENT, FileEffect.of("""
                        Please, provide link to your excel file in google drive.
                        File must be shared with editor rights.
                        Image may at help.
                        """,
                SHARE_INSTRUCTIONS_PNG,
                ContentType.PNG,
                readResourceFile(DocumentLinkHandler.class, "/img/" + SHARE_INSTRUCTIONS_PNG)
                        .orElseThrow(() -> new CustomException("No instruction image."))
        ), PrincipalContext.isInitiated());
    }

    @Override
    public StateDescriptor stateDescriptor() {
        return PROVIDE_LINK_TO_THE_DOCUMENT;
    }
}
