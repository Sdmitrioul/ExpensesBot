package com.dskroba.statemachine.handlers.expense;

import com.dskroba.actor.StateContext;
import com.dskroba.expenses.Expense;
import com.dskroba.expenses.ExpenseUtils;
import com.dskroba.statemachine.effects.Effect;
import com.dskroba.statemachine.events.Event;
import com.dskroba.statemachine.handlers.AbstractHandler;
import com.dskroba.statemachine.state.State;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static com.dskroba.statemachine.state.ExpenseStates.*;

@Component
public class AddAmountHandler extends AbstractHandler {
    @Override
    protected Optional<List<Effect<?>>> handleStateEvents(Event<?> event) {
        return handleMessageEvent(event)
                .map(amount -> amount.replace(",", "."))
                .flatMap(ExpenseUtils::parseAmount)
                .map(this::processAmount);
    }

    private List<Effect<?>> processAmount(Double amount) {
        StateContext stateContext = StateContext.PROVIDER.get();
        stateContext.getAttribute(EXPENSE_ATTRIBUTE_NAME, Expense.Builder.class)
                .amount(amount);
        stateContext.setState(ADD_DATE);
        return List.of(ADD_DATE.initialEffect());
    }

    @Override
    public State state() {
        return ADD_AMOUNT;
    }
}