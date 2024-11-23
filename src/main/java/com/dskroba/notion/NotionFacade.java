package com.dskroba.notion;

import com.dskroba.type.Expense;

import java.util.Date;
import java.util.List;

public interface NotionFacade {
    boolean insertExpense(Expense expense);
    List<Expense> getExpenses(Date from, Date to);
}
