package com.dskroba.principals;

import com.dskroba.google.GoogleTable;
import com.dskroba.telegram.TelegramId;
import com.dskroba.type.Currency;

import java.util.List;

public record Principal(
        TelegramId key,
        PrincipalContext context
) {
    public static final Long NONEXISTING_TABLE = -1L;

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
        private PrincipalContext context;

        public Builder telegramId(TelegramId telegramId) {
            this.key = telegramId;
            return this;
        }

        public Builder telegramId(Long telegramId, String handle, Long chatId) {
            this.key = new TelegramId(telegramId, handle, chatId);
            return this;
        }

        public Builder context(PrincipalContext context) {
            this.context = context;
            return this;
        }

        public Builder context(List<GoogleTable> tables, Currency activeCurrency, String activeTableId) {
            this.context = new PrincipalContext(tables, activeCurrency, activeTableId);
            return this;
        }

        public Builder tables(List<GoogleTable> tables) {
            this.context = new PrincipalContext(
                    tables,
                    context != null ? context.activeCurrency() : null,
                    context != null ? context.activeTableId() : null
            );
            return this;
        }

        public Builder activeCurrency(Currency currency) {
            this.context = new PrincipalContext(
                    context != null ? context.tables() : null,
                    currency,
                    context != null ? context.activeTableId() : null
            );
            return this;
        }

        public Builder activeTableId(String tableId) {
            this.context = new PrincipalContext(
                    context != null ? context.tables() : null,
                    context != null ? context.activeCurrency() : null,
                    tableId
            );
            return this;
        }

        public Principal build() {
            return new Principal(key, context);
        }
    }
}
