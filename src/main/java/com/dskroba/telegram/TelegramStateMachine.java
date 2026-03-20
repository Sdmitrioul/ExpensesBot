package com.dskroba.telegram;

import com.dskroba.actor.ActorManager;
import com.dskroba.statemachine.AuthorizationModule;
import com.dskroba.statemachine.StateMachine;
import com.dskroba.statemachine.handlers.EventHandlerRegistry;
import com.pengrad.telegrambot.model.Update;

public class TelegramStateMachine extends StateMachine<Update> {
    public TelegramStateMachine(TelegramIOModule ioModule,
                                EventHandlerRegistry handlerRegistry,
                                AuthorizationModule authorizationService,
                                ActorManager actorManager) {
        super(ioModule, handlerRegistry, authorizationService, actorManager);
    }
}
