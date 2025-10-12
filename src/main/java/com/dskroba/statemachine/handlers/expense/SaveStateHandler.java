package com.dskroba.statemachine.handlers.expense;

import com.dskroba.actor.PrincipalContext;
import com.dskroba.actor.StateContext;
import com.dskroba.expenses.Expense;
import com.dskroba.expenses.ExpenseRepository;
import com.dskroba.statemachine.effects.Effect;
import com.dskroba.statemachine.effects.MessageEffect;
import com.dskroba.statemachine.events.Event;
import com.dskroba.statemachine.handlers.AbstractHandler;
import com.dskroba.statemachine.state.ExpenseStates;
import com.dskroba.statemachine.state.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.dskroba.statemachine.state.ExpenseStates.CHOOSE_TAG;
import static com.dskroba.statemachine.state.ExpenseStates.EXPENSE_ATTRIBUTE_NAME;

@Component
public class SaveStateHandler extends AbstractHandler {
    private final ExpenseRepository repository;

    @Autowired
    public SaveStateHandler(ExpenseRepository repository) {
        this.repository = repository;
    }

    @Override
    protected final Optional<List<Effect<?>>> handleStateEvents(Event<?> event) {
        return handleMessageEvent(event)
                .filter("save"::equalsIgnoreCase)
                .map(_ -> this.saveExpense())
                .or(() -> tryUpdateExpense(event));
    }

    protected Optional<List<Effect<?>>> tryUpdateExpense(Event<?> event) {
        return Optional.empty();
    }

    private List<Effect<?>> saveExpense() {
        var context = StateContext.PROVIDER.get();
        var builder = context.getAttribute(EXPENSE_ATTRIBUTE_NAME, Expense.Builder.class);
        builder.currency(PrincipalContext.currency());
        builder.groupId(PrincipalContext.group());
        context.clearAttributes();
        var success = repository.insertExpense(builder.build());
        List<Effect<?>> effects = new ArrayList<>();
        if (!success) {
            effects.add(new MessageEffect("There was an error inserting expense!\nPlease try again later."));
        }
        context.setState(CHOOSE_TAG);
        effects.add(CHOOSE_TAG.initialEffect());
        return effects;
    }


    @Override
    public State state() {
        return ExpenseStates.SAVE_EXPENSE;
    }
}
