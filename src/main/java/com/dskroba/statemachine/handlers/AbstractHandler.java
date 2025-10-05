package com.dskroba.statemachine.handlers;

import com.dskroba.actor.StateContext;
import com.dskroba.statemachine.effects.Effect;
import com.dskroba.statemachine.effects.MessageEffect;
import com.dskroba.statemachine.events.CommandEvent;
import com.dskroba.statemachine.events.Event;
import com.dskroba.statemachine.events.MessageEvent;
import com.dskroba.statemachine.state.CommandStates;
import com.dskroba.statemachine.state.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public abstract class AbstractHandler implements EventHandler {
    protected static final Logger log = LoggerFactory.getLogger(AbstractHandler.class);
    private static final Set<String> INITIAL_COMMANDS = Set.of("start", "help");

    @Override
    public List<Effect<?>> handleEvent(Event<?> event) {
        return tryHandleCommand(event)
                .or(() -> tryHandleCancelRequest(event))
                .or(() -> tryHandleStateEvents(event))
                .orElseGet(() -> unrecognizedEventHandler(event));
    }

    private Optional<List<Effect<?>>> tryHandleCommand(Event<?> event) {
        if (!(event instanceof CommandEvent commandEvent)) {
            return Optional.empty();
        }
        return Optional.of(handleCommand(commandEvent));
    }

    private Optional<List<Effect<?>>> tryHandleCancelRequest(Event<?> event) {
        if (!isCancelRequest(event) || (state() != null && !state().isCancellable())) {
            return Optional.empty();
        }
        return Optional.of(handleChangeOfState(CommandStates.MENU));
    }

    private Optional<List<Effect<?>>> tryHandleStateEvents(Event<?> event) {
        return handleStateEvents(event);
    }

    private static boolean isCancelRequest(Event<?> event) {
        return event instanceof MessageEvent messageEvent
                && "cancel".equalsIgnoreCase(messageEvent.payload());
    }

    private List<Effect<?>> unrecognizedEventHandler(Event<?> event) {
        log.warn("Unrecognized event: {}", event);
        List<Effect<?>> effects = new ArrayList<>();
        effects.add(new MessageEffect("Unrecognized option.\nPlease, try again."));
        effects.add(state().initialEffect());
        return effects;
    }

    private List<Effect<?>> handleCommand(CommandEvent commandEvent) {
        String command = commandEvent.payload();

        if (INITIAL_COMMANDS.contains(command.toLowerCase(Locale.ROOT))) {
            return createInitialCommandResponse();
        }

        return parseAndHandleStateCommand(command);
    }

    private List<Effect<?>> createInitialCommandResponse() {
        return List.of(
                new MessageEffect("This bot provides instruments to track expenses."),
                CommandStates.MENU.initialEffect()
        );
    }

    private List<Effect<?>> parseAndHandleStateCommand(String command) {
        try {
            CommandStates state = CommandStates.valueOf(command.toUpperCase(Locale.ROOT));
            return handleChangeOfState(state);
        } catch (IllegalArgumentException e) {
            log.warn("Unknown command received: {}", command);
            return createErrorResponse(
                    "Unknown command: " + command,
                    CommandStates.MENU.initialEffect()
            );
        }
    }

    protected List<Effect<?>> handleChangeOfState(State state) {
        StateContext stateContext = StateContext.PROVIDER.get();
        boolean hadAttributes = stateContext.attributeExists();

        stateContext.setState(state);
        stateContext.clearAttributes();

        return buildStateTransitionEffects(state, hadAttributes);
    }

    protected Optional<String> handleMessageEvent(Event<?> event) {
        return Optional.ofNullable(event)
                .filter(e -> e instanceof MessageEvent)
                .map(e -> (MessageEvent) e)
                .map(MessageEvent::payload);
    }

    private List<Effect<?>> buildStateTransitionEffects(State state, boolean hadPreviousData) {
        List<Effect<?>> effects = new ArrayList<>();
        if (hadPreviousData) {
            effects.add(new MessageEffect("Previously provided data will be deleted."));
        }
        effects.add(state.initialEffect());
        return effects;
    }

    protected List<Effect<?>> createErrorResponse(String message, Effect<?> fallbackEffect) {
        return List.of(new MessageEffect(message), fallbackEffect);
    }

    protected abstract Optional<List<Effect<?>>> handleStateEvents(Event<?> event);
}
