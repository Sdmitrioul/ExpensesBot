package com.dskroba.statemachine.handlers.expense;

import com.dskroba.actor.StateContext;
import com.dskroba.expenses.Expense;
import com.dskroba.expenses.ExpenseRepository;
import com.dskroba.statemachine.effects.Effect;
import com.dskroba.statemachine.events.Event;
import com.dskroba.statemachine.state.ExpenseStates;
import com.dskroba.statemachine.state.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static com.dskroba.statemachine.state.ExpenseStates.EXPENSE_ATTRIBUTE_NAME;
import static com.dskroba.statemachine.state.ExpenseStates.SAVE_EXPENSE;

@Component
public class AddDescriptionHandler extends SaveStateHandler {
    @Autowired
    public AddDescriptionHandler(ExpenseRepository repository) {
        super(repository);
    }

    @Override
    protected Optional<List<Effect<?>>> tryUpdateExpense(Event<?> event) {
        return handleMessageEvent(event)
                .map(this::processDescription);
    }

    private List<Effect<?>> processDescription(String description) {
        var stateContext = StateContext.PROVIDER.get();
        stateContext.getAttribute(EXPENSE_ATTRIBUTE_NAME, Expense.Builder.class)
                .description(description);
        stateContext.setState(SAVE_EXPENSE);
        return List.of(SAVE_EXPENSE.initialEffect());
    }

    @Override
    public State state() {
        return ExpenseStates.ADD_DESCRIPTION;
    }
}
