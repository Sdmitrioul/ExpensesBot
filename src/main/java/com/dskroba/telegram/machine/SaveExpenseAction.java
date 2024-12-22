package com.dskroba.telegram.machine;

import com.dskroba.telegram.machine.state.ExpenseSavableState;
import com.dskroba.type.Expense;

public class SaveExpenseAction implements Action {
    private final Expense.Builder expenseBuilder;
    private final State nextState;

    public SaveExpenseAction(Expense.Builder expenseBuilder, State nextState) {
        this.expenseBuilder = expenseBuilder;
        this.nextState = nextState;
    }

    @Override
    public boolean isFullRow() {
        return true;
    }

    @Override
    public int priority() {
        return Integer.MAX_VALUE / 2;
    }

    @Override
    public State execute(State previous) {
        if (previous instanceof ExpenseSavableState expenseSavableState) {
            expenseSavableState.save(expenseBuilder.build());
            return nextState;
        }
        return previous;
    }

    @Override
    public String getDescription() {
        return "Save expense";
    }
}
