package com.dskroba.principals;

import com.dskroba.google.GoogleTable;
import com.dskroba.telegram.TelegramId;
import com.dskroba.type.Currency;

import java.util.ArrayList;
import java.util.List;

public record Principal(
        TelegramId key,
        PrincipalData context
) {
    public static final GoogleTable NONEXISTING_TABLE = null;

    public Long id() {
        return key.telegramId();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(TelegramId telegramId) {
        return new Builder().telegramId(telegramId);
    }

    public static Builder builder(Principal source) {
        return new Builder()
                .telegramId(source.key())
                .context(source.context());
    }

    public Builder toBuilder() {
        return builder(this);
    }

    public static class Builder {
        private TelegramId key;
        private PrincipalData context;

        public Builder telegramId(TelegramId telegramId) {
            this.key = telegramId;
            return this;
        }

        public Builder telegramId(Long telegramId, String handle, Long chatId) {
            this.key = new TelegramId(telegramId, handle, chatId);
            return this;
        }

        public Builder context(PrincipalData context) {
            this.context = context;
            return this;
        }

        public Builder context(List<GoogleTable> tables, Currency activeCurrency, GoogleTable activeTable) {
            this.context = new PrincipalData(tables, activeCurrency, activeTable);
            return this;
        }

        public Builder tables(List<GoogleTable> tables) {
            this.context = new PrincipalData(
                    tables,
                    context != null ? context.activeCurrency() : null,
                    context != null ? context.activeTable() : null
            );
            return this;
        }

        public Builder table(GoogleTable table) {
            List<GoogleTable> tables = new ArrayList<>(context != null ? context.tables() : List.of());
            tables.add(table);
            this.context = new PrincipalData(
                    tables,
                    context != null ? context.activeCurrency() : null,
                    context != null ? context.activeTable() : null
            );
            return this;
        }

        public Builder activeCurrency(Currency currency) {
            this.context = new PrincipalData(
                    context != null ? context.tables() : null,
                    currency,
                    context != null ? context.activeTable() : null
            );
            return this;
        }

        public Builder activeTableId(GoogleTable table) {
            this.context = new PrincipalData(
                    context != null ? context.tables() : null,
                    context != null ? context.activeCurrency() : null,
                    table
            );
            return this;
        }

        public Principal build() {
            return new Principal(key, context);
        }
    }
}
