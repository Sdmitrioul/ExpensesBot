package com.dskroba.expenses;

import com.dskroba.type.Currency;

import java.util.Date;

public record Expense(ExpenseType expenseType,
                      double amount,
                      Currency currency,
                      String description,
                      Long issuer,
                      Date date) {
    public static class Builder {
        private ExpenseType type = ExpenseType.MISCELLANEOUS;
        private double amount;
        private Currency currency;
        private String description;
        private Long issuerId;
        private Date date;

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

        public Builder type(ExpenseType type) {
            this.type = type;
            return this;
        }

        public Builder type(String type) {
            try {
                this.type = ExpenseType.valueOf(type);
            } catch (IllegalArgumentException e) {
                // ignore
            }
            return this;
        }

        public Builder issuerId(Long id) {
            this.issuerId = id;
            return this;
        }

        public Builder currency(Currency currency) {
            this.currency = currency;
            return this;
        }

        public Builder currency(String currency) {
            this.currency = Currency.parseValue(currency);
            return this;
        }

        public Currency currency() {
            return this.currency;
        }

        public ExpenseType getType() {
            return type;
        }

        public double getAmount() {
            return amount;
        }

        public Currency getCurrency() {
            return currency;
        }

        public String getDescription() {
            return description;
        }

        public Long getIssuerId() {
            return issuerId;
        }

        public Date getDate() {
            return date;
        }

        public Expense build() {
            return new Expense(type, amount, currency, description, issuerId, date);
        }
    }
}
