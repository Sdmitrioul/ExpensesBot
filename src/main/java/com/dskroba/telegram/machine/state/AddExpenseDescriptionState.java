package com.dskroba.telegram.machine.state;

import com.dskroba.telegram.UserContext;
import com.dskroba.telegram.machine.Action;
import com.dskroba.telegram.machine.CancelAction;
import com.dskroba.telegram.machine.SaveExpenseAction;
import com.dskroba.telegram.machine.State;
import com.dskroba.type.Expense;

import java.util.List;
import java.util.Optional;

public class AddExpenseDescriptionState extends ExpenseSavableState {
    private final List<Action> actions;

    public AddExpenseDescriptionState(UserContext context, Expense.Builder expenseBuilder) {
        super("Add description", context, expenseBuilder);
        this.actions = List.of(new SaveExpenseAction(expenseBuilder, StateFactory.getStartState(context)),
                CancelAction.create(context), new AddExpenseDescriptionAction(expenseBuilder));
    }

    @Override
    protected Action parseCustomAction(String text) {
        return new AddExpenseDescriptionAction(expenseBuilder.description(text));
    }

    @Override
    public List<Action> getAllowedActions() {
        return actions;
    }

    @Override
    public Optional<String> message() {
        return Optional.of("Add description if needed");
    }

    private record AddExpenseDescriptionAction(Expense.Builder expenseBuilder) implements Action {
        @Override
        public State execute(State previous) {
            if (previous instanceof AddExpenseDescriptionState addExpenseDescriptionState) {
                return new AddExpenseDateState(addExpenseDescriptionState.context, expenseBuilder);
            }
            return previous;
        }


        @Override
        public boolean isHidden() {
            return true;
        }

        @Override
        public String getDescription() {
            return "Add description";
        }
    }
}
