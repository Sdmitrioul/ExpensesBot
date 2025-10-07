package com.dskroba.expenses;

import com.dskroba.type.Currency;
import com.dskroba.type.Timeframe;

import java.util.List;

public interface ExpenseRepository {
    boolean insertExpense(Expense expense);

    List<Expense> getExpenses(Timeframe timeframe, Long issuerId, Currency currency);

    default boolean insertExpense(List<Expense> expense) {
        return expense.stream().allMatch(this::insertExpense);
    }
}
