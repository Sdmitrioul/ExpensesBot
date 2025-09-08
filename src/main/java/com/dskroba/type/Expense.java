package com.dskroba.type;

import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.Date;
import java.util.Locale;

import static com.dskroba.notion.DatabaseUtil.INSTANT_TIME_FORMATTER;

public record Expense(double amount, Date date, String description, ExpenseTag expenseTag) {
    public static final String[] SHORT_HEADERS = new String[]{"Tag", "Amount", "Description"};
    public static final String[] AGGREGATED_HEADERS = new String[]{"Tag", "Amount"};

    public String getFistTag() {
        return getTagName("Undefined");
    }

    public String getTagName(String defaultValue) {
        return expenseTag != null ? expenseTag.name() : defaultValue;
    }

    public ExpenseTag getTag() {
        return this.expenseTag;
    }

    public String getMonth() {
        return fromDate(date());
    }

    private String fromDate(Date date) {
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
                .getMonth()
                .getDisplayName(TextStyle.FULL, Locale.ENGLISH);
    }

    public String getNote() {
        return this.description;
    }

    @Override
    public String toString() {
        return "Expense{\n" +
                "\tamount=" + amount + ",\n" +
                "\texpenseTag=" + getTagName("null") + ",\n" +
                "\tdate=" + INSTANT_TIME_FORMATTER.format(date.toInstant()) + ",\n" +
                "\tdescription='" + description + '\'' + ",\n" +
                '}';
    }

    public static class Builder {
        private double amount;
        private Date date;
        private String description;
        private ExpenseTag type = ExpenseTag.MISCELLANEOUS;

        public Builder() {
        }

        public Builder amount(double amount) {
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

        public Builder setType(ExpenseTag type) {
            this.type = type;
            return this;
        }

        public Builder setType(String type) {
            try {
                this.type = ExpenseTag.valueOf(type);
            } catch (IllegalArgumentException e) {
                // ignore
            }
            return this;
        }

        public Expense build() {
            return new Expense(amount, date, description, type);
        }
    }
}
