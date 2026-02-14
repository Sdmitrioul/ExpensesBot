package com.dskroba.statemachine.handlers.google;

import com.dskroba.actor.PrincipalContext;
import com.dskroba.actor.StateContext;
import com.dskroba.google.GoogleTable;
import com.dskroba.principals.Principal;
import com.dskroba.principals.PrincipalService;
import com.dskroba.statemachine.effects.Effect;
import com.dskroba.statemachine.effects.MessageEffect;
import com.dskroba.statemachine.effects.SelectEffect;
import com.dskroba.statemachine.events.Event;
import com.dskroba.statemachine.handlers.AbstractHandlerWithFactory;
import com.dskroba.statemachine.state.State;
import com.dskroba.statemachine.state.StateRecord;
import com.dskroba.statemachine.state.descriptors.StateDescriptor;
import com.dskroba.type.SelectOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static com.dskroba.statemachine.state.descriptors.GoogleStatesDescriptors.*;

@Component
public class SaveDocumentHandler extends AbstractHandlerWithFactory {
    private static final String CHANGE_SHEET_NAME = "Change sheet name";
    private static final List<SelectOption> ACKNOWLEDGMENT_OPTIONS = List.of(SelectOption.SAVE_OPTION, SelectOption.base(CHANGE_SHEET_NAME));

    private final PrincipalService principalService;

    @Autowired
    public SaveDocumentHandler(PrincipalService principalService) {
        this.principalService = principalService;
    }

    @Override
    protected Optional<Effect> handleStateEvents(Event event) {
        return handleMessageEvent(event)
                .map(this::processCommand);
    }

    private Effect processCommand(String command) {
        if (CHANGE_SHEET_NAME.equalsIgnoreCase(command.trim())) {
            return moveToState(PROVIDE_SHEET_NAME);
        }
        if ("save".equalsIgnoreCase(command)) {
            GoogleTable table = GoogleTable.fromLink(getDocumentLink(), getSheetName());
            var principal = PrincipalContext.PROVIDER.get().getPrincipal();
            var updatedPrincipal = Principal.builder(principal).table(table).activeTableId(table.documentId()).build();
            updatedPrincipal = principalService.savePrincipal(updatedPrincipal);
            PrincipalContext.PROVIDER.get().setPrincipal(updatedPrincipal);
            return MessageEffect.of("Document saved as current storage!").composite(switchToMenu());
        }
        return null;
    }

    private static String getDocumentLink() {
        return StateContext.getContext().getAttribute(SHEET_DOCUMENT_LINK_ATTRIBUTE, String.class);
    }

    private static String getSheetName() {
        return StateContext.getContext().getAttribute(SHEET_NAME_ATTRIBUTE, String.class);
    }

    @Override
    public State createState() {
        String documentLink = getDocumentLink();
        String sheetName = getSheetName();
        String message = """
                Save next google table as your storage:
                Sheet name %s
                Document link: %s
                """.formatted(sheetName, documentLink);
        Effect effect;
        boolean isCancelable = PrincipalContext.isInitiated();
        if (isCancelable) {
            effect = SelectEffect.withCancelOption(message, ACKNOWLEDGMENT_OPTIONS);
        } else {
            effect = SelectEffect.of(message, ACKNOWLEDGMENT_OPTIONS);
        }
        return new StateRecord(SAVE_GOOGLE_SHEET, effect, isCancelable);
    }

    @Override
    public StateDescriptor stateDescriptor() {
        return SAVE_GOOGLE_SHEET;
    }
}
