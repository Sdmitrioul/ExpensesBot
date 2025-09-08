package com.dskroba.repository;

import com.dskroba.base.type.Pair;
import com.dskroba.type.AppUser;
import com.dskroba.type.Expense;

import java.time.Instant;
import java.util.List;

public interface ExpenseRepository {
    AppUser addUser(AppUser user);

    List<AppUser> getAllUser();

    boolean insertExpense(Expense expense);

    List<Expense> getExpenses(Pair<Instant, Instant> timeInterval);

    default boolean insertExpense(List<Expense> expense) {
        return expense.stream().allMatch(this::insertExpense);
    }
}
