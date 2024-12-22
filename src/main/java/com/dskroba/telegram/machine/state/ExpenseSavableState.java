package com.dskroba.telegram.machine.state;

import com.dskroba.telegram.UserContext;
import com.dskroba.telegram.machine.Action;
import com.dskroba.type.Expense;

public abstract class ExpenseSavableState extends ExpenseBuilderState {
    protected ExpenseSavableState(String name, UserContext context, Expense.Builder expenseBuilder) {
        super(name, context, expenseBuilder);
    }

    public void save(Expense expense) {
        context.saveExpense(expense);
    }

    @Override
    public Action parseAction(String text) {
        Action action = super.parseAction(text);
        if (action != null) {
            return action;
        }
        return parseCustomAction(text);
    }

    protected abstract Action parseCustomAction(String text);
}
