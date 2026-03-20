package com.dskroba.statemachine.handlers.expense;

import com.dskroba.actor.Context;
import com.dskroba.expenses.Expense;
import com.dskroba.expenses.ExpenseRepository;
import com.dskroba.statemachine.effects.Effect;
import com.dskroba.statemachine.effects.SelectEffect;
import com.dskroba.statemachine.events.Event;
import com.dskroba.statemachine.state.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static com.dskroba.statemachine.state.descriptors.ExpensesStatesDescriptors.*;
import static com.dskroba.type.SelectOption.SAVE_OPTION;

@Component
public class AddDescriptionHandler extends SaveStateHandler {
    @Autowired
    public AddDescriptionHandler(ExpenseRepository repository) {
        super(repository, State.cancelable(ADD_DESCRIPTION,
                SelectEffect.withCancelOption("Add description, if needed", List.of(SAVE_OPTION))));
    }

    @Override
    protected Optional<Effect> tryUpdateExpense(Event event) {
        return handleMessageEvent(event)
                .map(this::processDescription);
    }

    private Effect processDescription(String description) {
        var stateContext = Context.PROVIDER.get();
        stateContext.getAttribute(EXPENSE_ATTRIBUTE_NAME, Expense.Builder.class)
                .description(description);
        return moveToState(SAVE_EXPENSE);
    }
}
