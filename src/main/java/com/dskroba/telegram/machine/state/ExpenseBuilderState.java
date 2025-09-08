package com.dskroba.telegram.machine.state;

import com.dskroba.base.ClockProvider;
import com.dskroba.telegram.UserContext;
import com.dskroba.type.Expense;

import java.util.Date;

public abstract class ExpenseBuilderState extends AbstractState {
    protected final Expense.Builder expenseBuilder;

    protected ExpenseBuilderState(String name, UserContext context) {
        this(name, context, new Expense.Builder().date(Date.from(ClockProvider.getClock().instant())));
    }

    protected ExpenseBuilderState(String name, UserContext context, Expense.Builder expenseBuilder) {
        super(name, context);
        this.expenseBuilder = expenseBuilder;
    }
}
