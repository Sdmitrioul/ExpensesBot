package com.dskroba.services;

import com.dskroba.base.ClockProvider;
import com.dskroba.expenses.ExpenseRepository;
import com.dskroba.type.DateFilter;
import com.dskroba.expenses.Expense;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlottingService {
    private final ExpenseRepository repository;

    @Autowired
    public PlottingService(ExpenseRepository repository) {
        this.repository = repository;
    }

    public void createPieChart(DateFilter filter) {
        List<Expense> expenses = repository.getExpenses(filter.getTimeIntervals(ClockProvider.getClock()));
    }
}
