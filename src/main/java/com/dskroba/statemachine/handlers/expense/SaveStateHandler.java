package com.dskroba.statemachine.handlers.expense;

import com.dskroba.actor.Context;
import com.dskroba.expenses.Expense;
import com.dskroba.expenses.ExpenseRepository;
import com.dskroba.statemachine.effects.Effect;
import com.dskroba.statemachine.effects.MessageEffect;
import com.dskroba.statemachine.effects.SelectEffect;
import com.dskroba.statemachine.events.Event;
import com.dskroba.statemachine.handlers.AbstractHandlerWithFactory;
import com.dskroba.statemachine.state.ContextAccessor;
import com.dskroba.statemachine.state.State;
import com.dskroba.statemachine.state.descriptors.StateDescriptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static com.dskroba.statemachine.state.descriptors.ExpensesStatesDescriptors.*;
import static com.dskroba.type.SelectOption.SAVE_OPTION;

@Component
public class SaveStateHandler extends AbstractHandlerWithFactory {
    private final ExpenseRepository repository;
    private final State stateInstance;

    @Autowired
    public SaveStateHandler(ExpenseRepository repository) {
        this(repository, State.cancelable(
                SAVE_EXPENSE,
                SelectEffect.withCancelOption("Save or Discard?", List.of(SAVE_OPTION))));
    }

    SaveStateHandler(ExpenseRepository repository, State stateInstance) {
        this.repository = repository;
        this.stateInstance = stateInstance;
    }

    @Override
    protected final Optional<Effect> handleStateEvents(Event event) {
        return handleMessageEvent(event)
                .filter("save"::equalsIgnoreCase)
                .flatMap(_ -> this.saveExpense())
                .or(() -> tryUpdateExpense(event));
    }

    protected Optional<Effect> tryUpdateExpense(Event event) {
        return Optional.empty();
    }

    private Optional<Effect> saveExpense() {
        var context = Context.PROVIDER.get();
        var builder = context.getAttribute(EXPENSE_ATTRIBUTE_NAME, Expense.Builder.class);
        builder.currency(ContextAccessor.principalCurrency());
        var success = repository.insertExpense(builder.build(), ContextAccessor.principalStorage());
        if (!success) {
            return Optional.of(MessageEffect.of("""
                    There was an error inserting expense!
                    Please try again later.""").composite(switchToState(CHOOSE_TAG)));
        }
        return Optional.of(switchToMenu());
    }


    @Override
    public State createState() {
        return stateInstance;
    }

    @Override
    public StateDescriptor stateDescriptor() {
        return stateInstance.descriptor();
    }
}
