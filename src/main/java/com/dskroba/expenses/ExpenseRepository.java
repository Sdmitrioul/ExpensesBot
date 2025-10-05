package com.dskroba.expenses;

import com.dskroba.base.type.Pair;

import java.time.Instant;
import java.util.List;

public interface ExpenseRepository {
    boolean insertExpense(Expense expense);

    List<Expense> getExpenses(Pair<Instant, Instant> timeInterval);

    default boolean insertExpense(List<Expense> expense) {
        return expense.stream().allMatch(this::insertExpense);
    }
}
