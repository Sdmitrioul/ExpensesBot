package com.dskroba.type;

import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static com.dskroba.notion.DatabaseUtil.INSTANT_TIME_FORMATTER;

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

    @Override
    public String toString() {
        return "Expense{\n" +
                "\tamount=" + amount + ",\n" +
                "\tdate=" + INSTANT_TIME_FORMATTER.format(date.toInstant()) + ",\n" +
                "\tdescription='" + description + '\'' + ",\n" +
                "\texpenseTags=" + expenseTags
                .stream()
                .map(Enum::toString)
                .collect(Collectors.joining(",", "[", "]")) + ",\n" +
                '}';
    }

    public static class Builder {
        private int amount;
        private Date date;
        private String description;
        private final List<ExpenseTag> expenseTags = new ArrayList<>();

        public Builder() {
        }

        public Builder amount(int amount) {
            this.amount = amount;
            return this;
        }

        public Builder date(Date date) {
            this.date = date;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder putTag(ExpenseTag tag) {
            this.expenseTags.add(tag);
            return this;
        }

        public Expense build() {
            return new Expense(amount, date, description, expenseTags);
        }
    }
}
