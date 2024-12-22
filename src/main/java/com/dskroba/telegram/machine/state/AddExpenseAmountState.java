package com.dskroba.telegram.machine.state;

import com.dskroba.telegram.UserContext;
import com.dskroba.telegram.machine.*;
import com.dskroba.type.Expense;

import java.util.List;

public class AddExpenseAmountState extends ExpenseBuilderState {
    private final List<Action> allowedActions;

    public AddExpenseAmountState(UserContext context, Expense.Builder builder) {
        super("Add amount of expense", context, builder);
        this.allowedActions = List.of(new AddAmountToExpenseAction(0.), CancelAction.create(context));
    }

    @Override
    public Action parseAction(String text) {
        if (CancelAction.NAME.equals(text)) {
            return CancelAction.create(context);
        }
        try {
            Double value = Double.parseDouble(text);
            return new AddAmountToExpenseAction(value);
        } catch (NumberFormatException | NullPointerException e) {
            return new TryAgainAction("Invalid expense amount!");
        }
    }

    @Override
    public void initState() throws StateMachineException {
        context.makeResponse("Add amount of expense", message -> message
                .replyMarkup(keyboardMarkup(List.of(allowedActions.get(1)))));
    }

    @Override
    public List<Action> getAllowedActions() {
        return allowedActions;
    }

    private record AddAmountToExpenseAction(Double amount) implements Action {
        @Override
        public State execute(State previousState) {
            if (previousState instanceof AddExpenseAmountState previous) {
                return new AddExpenseDescriptionState(previous.context, previous.expenseBuilder.amount(amount));
            }
            return null;
        }

        @Override
        public boolean isHidden() {
            return true;
        }

        @Override
        public String getDescription() {
            return "Insert amount to expense";
        }
    }
}
