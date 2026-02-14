package com.dskroba.statemachine.handlers;

import com.dskroba.actor.PrincipalContext;
import com.dskroba.actor.StateContext;
import com.dskroba.statemachine.effects.Effect;
import com.dskroba.statemachine.effects.MessageEffect;
import com.dskroba.statemachine.events.CommandEvent;
import com.dskroba.statemachine.events.Event;
import com.dskroba.statemachine.events.MessageEvent;
import com.dskroba.statemachine.state.State;
import com.dskroba.statemachine.state.descriptors.CommandStatesDescriptors;
import com.dskroba.statemachine.state.descriptors.StateDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import static com.dskroba.statemachine.state.descriptors.CommandStatesDescriptors.*;

public abstract class AbstractHandler implements EventHandler, EventHandlerRegistryAware {
    protected static final Logger log = LoggerFactory.getLogger(AbstractHandler.class);
    private static final String START_COMMAND = "start";
    private static final Set<String> INITIAL_COMMANDS = Set.of(START_COMMAND, "help");
    private EventHandlerRegistry registry;

    @Override
    public Effect handleEvent(Event event) {
        return tryHandleCommand(event)
                .or(() -> tryHandleCancelRequest(event))
                .or(() -> tryHandleStateEvents(event))
                .orElseGet(() -> unrecognizedEventHandler(event));
    }

    @Override
    public void setRegistry(EventHandlerRegistry registry) {
        this.registry = registry;
    }

    private Optional<Effect> tryHandleCommand(Event event) {
        if (!(event instanceof CommandEvent commandEvent)) {
            return Optional.empty();
        }
        return Optional.of(handleCommand(commandEvent));
    }

    private Optional<Effect> tryHandleCancelRequest(Event event) {
        State state = StateContext.getCurrentState();
        if (!isCancelRequest(event) || (state != null && !state.isCancellable())) {
            return Optional.empty();
        }
        return Optional.of(switchToMenu());
    }

    private Optional<Effect> tryHandleStateEvents(Event event) {
        return handleStateEvents(event);
    }

    private static boolean isCancelRequest(Event event) {
        return event instanceof MessageEvent messageEvent
                && "cancel".equalsIgnoreCase(messageEvent.payload());
    }

    private Effect unrecognizedEventHandler(Event event) {
        log.warn("Unrecognized event: {}", event);
        return MessageEffect.of("Unrecognized option.\nPlease, try again.")
                .composite(StateContext.getContext().getState().initialEffect());
    }

    private Effect handleCommand(CommandEvent commandEvent) {
        String command = commandEvent.payload();

        if (START_COMMAND.equalsIgnoreCase(command) && !PrincipalContext.isInitiated()) {
            return startEffects();
        }

        if (INITIAL_COMMANDS.contains(command.toLowerCase(Locale.ROOT))) {
            return MessageEffect.of("This bot provides interface to track expenses in google sheet.")
                    .composite(switchToMenu());
        }

        return parseAndHandleStateCommand(command);
    }

    private Effect startEffects() {
        return moveToState(PROVIDE_LINK_TO_THE_DOCUMENT);
    }

    private Effect parseAndHandleStateCommand(String command) {
        try {
            CommandStatesDescriptors state = valueOf(command.toUpperCase(Locale.ROOT));
            return moveToState(state);
        } catch (IllegalArgumentException e) {
            log.warn("Unknown command received: {}", command);
            return MessageEffect.of("Unknown command: " + command)
                    .composite(switchToMenu());
        }
    }

    protected Optional<String> handleMessageEvent(Event event) {
        return Optional.ofNullable(event)
                .filter(e -> e instanceof MessageEvent)
                .map(e -> (MessageEvent) e)
                .map(MessageEvent::payload);
    }

    protected Effect switchToMenu() {
        return handleChangeOfState(MENU, true);
    }

    protected Effect switchToState(StateDescriptor stateDescriptor) {
        return handleChangeOfState(stateDescriptor, true);
    }

    protected Effect moveToState(StateDescriptor stateDescriptor) {
        return handleChangeOfState(stateDescriptor, false);
    }

    private Effect handleChangeOfState(StateDescriptor stateDescriptor, boolean clearContext) {
        State nextState = registry.getFactory(stateDescriptor).createState();

        StateContext stateContext = StateContext.getContext();
        if (clearContext) {
            stateContext.clearAttributes();
        }
        stateContext.setState(nextState);
        return buildStateTransitionEffects(nextState);
    }

    private Effect buildStateTransitionEffects(State state) {
        return state.initialEffect();
    }

    protected abstract Optional<Effect> handleStateEvents(Event event);
}
