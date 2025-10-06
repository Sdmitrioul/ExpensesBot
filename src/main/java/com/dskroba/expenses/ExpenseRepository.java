package com.dskroba.expenses;

import com.dskroba.base.type.Pair;
import com.dskroba.type.Timeframe;

import java.time.Instant;
import java.util.List;

public interface ExpenseRepository {
    boolean insertExpense(Expense expense);

    List<Expense> getExpenses(Timeframe timeframe, Long issuerId);

    default boolean insertExpense(List<Expense> expense) {
        return expense.stream().allMatch(this::insertExpense);
    }
}
