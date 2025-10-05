package com.dskroba.expenses;

import com.dskroba.type.Currency;

import java.util.Date;

public record Expense(Long id,
                      double amount,
                      Long issuer,
                      Date date,
                      String description,
                      ExpenseTag expenseTag,
                      Currency currency) {
    public static class Builder {
        private Long id;
        private double amount;
        private Date date;
        private String description;
        private ExpenseTag type = ExpenseTag.MISCELLANEOUS;
        private Currency currency;
        private Long issuerId;

        public Builder() {
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
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

        public Builder type(ExpenseTag type) {
            this.type = type;
            return this;
        }

        public Builder type(String type) {
            try {
                this.type = ExpenseTag.valueOf(type);
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

        public Expense build() {
            return new Expense(id, amount, issuerId, date, description, type, currency);
        }
    }
}
