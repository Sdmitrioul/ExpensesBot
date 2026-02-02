package com.dskroba.expenses;

import com.dskroba.base.exception.RepositoryException;
import com.dskroba.google.GoogleTable;
import com.dskroba.type.Currency;
import com.dskroba.type.Timeframe;

import java.util.List;

public interface ExpenseRepository {
    default boolean insertExpense(Expense expense, GoogleTable googleTable) throws RepositoryException {
        return insertExpense(List.of(expense), googleTable);
    }

    List<Expense> getExpenses(Timeframe timeframe, Currency currency, GoogleTable googleTable) throws RepositoryException;

    boolean insertExpense(List<Expense> expense, GoogleTable googleTable);
}
