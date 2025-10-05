package com.dskroba.statemachine.handlers.command;

import com.dskroba.actor.PrincipalContext;
import com.dskroba.actor.StateContext;
import com.dskroba.principals.Principal;
import com.dskroba.principals.PrincipalService;
import com.dskroba.statemachine.effects.Effect;
import com.dskroba.statemachine.events.Event;
import com.dskroba.statemachine.handlers.AbstractHandler;
import com.dskroba.statemachine.state.CommandStates;
import com.dskroba.statemachine.state.State;
import com.dskroba.type.Currency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class SetActiveCurrencyHandler extends AbstractHandler {
    private final PrincipalService principalService;

    @Autowired
    public SetActiveCurrencyHandler(PrincipalService principalService) {
        this.principalService = principalService;
    }

    @Override
    protected Optional<List<Effect<?>>> handleStateEvents(Event<?> event) {
        return handleMessageEvent(event)
                .map(this::parseCurrency)
                .map(this::processCurrency);
    }

    private List<Effect<?>> processCurrency(Currency currency) {
        var context = PrincipalContext.PROVIDER.get();
        var updatedPrincipal = Principal.Builder.from(context.getPrincipal())
                .currency(currency)
                .build();
        context.setPrincipal(principalService.savePrincipal(updatedPrincipal));
        StateContext.PROVIDER.get().setState(CommandStates.MENU);
        return List.of(CommandStates.MENU.initialEffect());
    }

    private Currency parseCurrency(String currency) {
        try {
            return Currency.valueOf(currency);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public State state() {
        return CommandStates.SET_ACTIVE_CURRENCY;
    }
}
