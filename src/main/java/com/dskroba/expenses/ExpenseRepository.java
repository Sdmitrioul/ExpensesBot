package com.dskroba.expenses;

import com.dskroba.base.exception.RepositoryException;
import com.dskroba.type.Currency;
import com.dskroba.type.Timeframe;

import java.util.List;

public interface ExpenseRepository {
    boolean insertExpense(Expense expense) throws RepositoryException;

    List<Expense> getExpenses(Timeframe timeframe, Long groupId, Currency currency) throws RepositoryException;

    default boolean insertExpense(List<Expense> expense) throws RepositoryException {
        return expense.stream().allMatch(this::insertExpense);
    }
}
