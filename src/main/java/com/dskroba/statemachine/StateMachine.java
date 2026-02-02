package com.dskroba.statemachine;

import com.dskroba.actor.ActorManager;
import com.dskroba.actor.JobException;
import com.dskroba.actor.PrincipalActor;
import com.dskroba.actor.StateContext;
import com.dskroba.principals.Principal;
import com.dskroba.statemachine.effects.Effect;
import com.dskroba.statemachine.events.Event;
import com.dskroba.statemachine.handlers.EventHandler;
import com.dskroba.statemachine.handlers.EventHandlerRegistry;
import com.dskroba.telegram.TelegramId;

import java.util.List;

public abstract class StateMachine<I> {
    private final IOModule<I> ioModule;
    private final EventHandlerRegistry handlerRegistry;
    protected final AuthorizationModule authorizationModule;
    private final ActorManager actorManager;

    protected StateMachine(IOModule<I> ioModule,
                           EventHandlerRegistry handlerRegistry,
                           AuthorizationModule authorizationModule,
                           ActorManager actorManager) {
        this.ioModule = ioModule;
        this.handlerRegistry = handlerRegistry;
        this.authorizationModule = authorizationModule;
        this.actorManager = actorManager;
    }

    public final void processEvent(I input) throws AuthorizationException {
        TelegramId issuer = ioModule.provideIssuer(input);
        Principal authorizedIssuer = authorizationModule.authorize(issuer);
        PrincipalActor actor = actorManager.getActor(authorizedIssuer);
        actor.submit(() -> {
            try {
                Event<?> parsedEvent = ioModule.parse(input, authorizedIssuer);
                StateContext stateContext = StateContext.PROVIDER.get();
                EventHandler eventHandler = handlerRegistry.getHandler(stateContext.getState());
                List<Effect<?>> effects = eventHandler.handleEvent(parsedEvent);
                ioModule.handleEffects(effects);
            } catch (Exception e) {
                throw new JobException(e);
            }
        });
    }
}
