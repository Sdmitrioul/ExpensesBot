package com.dskroba.notion;

import com.dskroba.base.type.Pair;
import com.dskroba.type.Expense;

import java.time.Instant;
import java.util.List;

public interface NotionFacade {
    boolean insertExpense(Expense expense);
    List<Expense> getExpenses(Pair<Instant, Instant> timeInterval);
}
