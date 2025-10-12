package com.dskroba.expenses;

import com.dskroba.type.Currency;

import java.util.Date;

public record Expense(Long id,
                      ExpenseType expenseType,
                      double amount,
                      Currency currency,
                      String description,
                      Long issuer,
                      Long group,
                      Date date) {
    public static class Builder {
        private Long id;
        private ExpenseType type = ExpenseType.MISCELLANEOUS;
        private double amount;
        private Currency currency;
        private String description;
        private Long issuerId;
        private Long groupId;
        private Date date;

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

        public Builder groupId(Long id) {
            this.groupId = id;
            return this;
        }

        public Expense build() {
            return new Expense(id, type, amount, currency, description, issuerId, groupId, date);
        }
    }
}
