package com.dskroba.principals;

import com.dskroba.type.Currency;

import java.util.Objects;

public record Principal(PrincipalKey key, String code, Long chatId, Currency currency) {
    public boolean isResolved() {
        return key.isResolved();
    }

    public boolean isDifferent(Principal principal) {
        return !(Objects.equals(code, principal.code())
                && Objects.equals(chatId, principal.chatId())
                && Objects.equals(currency, principal.currency()));
    }

    public static class Builder {
        private Long id;
        private Long telegramId;
        private String code;
        private Long chatId;
        private Currency currency;

        public static Builder from(Principal principal) {
            PrincipalKey key = principal.key();
            Long id = key != null ? key.id() : null;
            Long telegramId = key != null ? key.telegramId() : null;
            return new Builder()
                    .id(id)
                    .telegramId(telegramId)
                    .code(principal.code())
                    .chatId(principal.chatId())
                    .currency(principal.currency());
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder telegramId(Long telegramId) {
            this.telegramId = telegramId;
            return this;
        }

        public Builder code(String code) {
            this.code = code;
            return this;
        }

        public Builder chatId(Long chatId) {
            this.chatId = chatId;
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

        public Builder merge(Principal principal) {
            PrincipalKey key = principal.key();
            if (key != null) {
                if (key.id() != null) {
                    this.id = key.id();
                }
                if (key.telegramId() != null) {
                    this.telegramId = key.telegramId();
                }
            }
            if (principal.currency() != null) {
                this.currency = principal.currency();
            }
            if (principal.code() != null) {
                this.code = principal.code();
            }
            if (principal.chatId() != null) {
                this.chatId = principal.chatId();
            }
            return this;
        }

        public Principal build() {
            return new Principal(new PrincipalKey(id, telegramId), code, chatId, currency);
        }
    }
}
