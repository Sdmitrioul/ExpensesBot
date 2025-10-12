package com.dskroba.statemachine.handlers.expense;

import com.dskroba.actor.PrincipalContext;
import com.dskroba.actor.StateContext;
import com.dskroba.base.ClockProvider;
import com.dskroba.expenses.Expense;
import com.dskroba.expenses.ExpenseType;
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
public class ChooseTagHandler extends AbstractHandler {
    @Override
    protected Optional<List<Effect<?>>> handleStateEvents(Event<?> event) {
        return handleMessageEvent(event)
                .map(String::toUpperCase)
                .flatMap(ExpenseUtils::parseTag)
                .map(this::processTag);
    }

    private List<Effect<?>> processTag(ExpenseType tag) {
        StateContext stateContext = StateContext.PROVIDER.get();
        stateContext.setAttribute(EXPENSE_ATTRIBUTE_NAME, new Expense.Builder()
                .type(tag)
                .issuerId(PrincipalContext.principalId())
                .date(ClockProvider.getCurrentDate()));
        stateContext.setState(ADD_AMOUNT);
        return List.of(ADD_AMOUNT.initialEffect());
    }

    @Override
    public State state() {
        return CHOOSE_TAG;
    }
}
