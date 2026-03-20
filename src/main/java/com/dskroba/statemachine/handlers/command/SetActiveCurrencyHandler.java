package com.dskroba.statemachine.handlers.command;

import com.dskroba.actor.Context;
import com.dskroba.principals.Principal;
import com.dskroba.principals.PrincipalService;
import com.dskroba.statemachine.effects.Effect;
import com.dskroba.statemachine.effects.SelectEffect;
import com.dskroba.statemachine.events.Event;
import com.dskroba.statemachine.handlers.AbstractHandlerWithFactory;
import com.dskroba.statemachine.state.ContextAccessor;
import com.dskroba.statemachine.state.State;
import com.dskroba.statemachine.state.descriptors.StateDescriptor;
import com.dskroba.type.Currency;
import com.dskroba.type.SelectOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.dskroba.statemachine.state.descriptors.CommandStatesDescriptors.SET_ACTIVE_CURRENCY;

@Component
public class SetActiveCurrencyHandler extends AbstractHandlerWithFactory {
    private final PrincipalService principalService;

    @Autowired
    public SetActiveCurrencyHandler(PrincipalService principalService) {
        this.principalService = principalService;
    }

    @Override
    protected Optional<Effect> handleStateEvents(Event event) {
        return handleMessageEvent(event)
                .map(this::parseCurrency)
                .map(this::processCurrency);
    }

    private Effect processCurrency(Currency currency) {
        Context context = ContextAccessor.context();
        Principal updatedPrincipal = context.getPrincipal().toBuilder()
                .activeCurrency(currency)
                .build();
        context.setPrincipal(updatedPrincipal);
        return switchToMenu();
    }

    private Currency parseCurrency(String currency) {
        try {
            if (currency.startsWith("[") && currency.endsWith("]")) {
                currency = currency.substring(1, currency.length() - 1);
            }
            return Currency.valueOf(currency);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public State createState() {
        List<SelectOption> options = new ArrayList<>();
        Currency activeCurrency = ContextAccessor.principalCurrency();
        for (Currency currency : Currency.values()) {
            String value = currency.name();
            if (activeCurrency == currency) {
                value = "[" + value + "]";
            }
            options.add(SelectOption.base(value));
        }
        return State.cancelable(
                SET_ACTIVE_CURRENCY,
                SelectEffect.withCancelOption("Select active currency.", options));
    }

    @Override
    public StateDescriptor stateDescriptor() {
        return SET_ACTIVE_CURRENCY;
    }
}
