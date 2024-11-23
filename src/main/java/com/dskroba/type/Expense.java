package com.dskroba.type;

import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Expense {
    private final int amount;
    private final Date date;
    private final String description;
    private final List<ExpenseTag> expenseTags;

    public Expense(int amount, Date date, String description, List<ExpenseTag> expenseTags) {
        this.amount = amount;
        this.date = date;
        this.description = description;
        this.expenseTags = expenseTags;
    }

    public int getAmount() {
        return amount;
    }

    public List<ExpenseTag> getTag() {
        return this.expenseTags;
    }

    public String getMonth() {
        return fromDate(getDate());
    }

    private String fromDate(Date date) {
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
                .getMonth()
                .getDisplayName(TextStyle.FULL, Locale.ENGLISH);
    }

    public Date getDate() {
        return this.date;
    }

    public String getNote() {
        return this.description;
    }
}
