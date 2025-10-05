package com.dskroba.type;

import java.util.Optional;

public enum Currency {
    EUR, USD, RUB;

    public static Currency parseValue(String currency) {
        return Optional.ofNullable(currency)
                .map(value -> {
                    try {
                        return Currency.valueOf(value);
                    } catch (IllegalArgumentException e) {
                        return EUR;
                    }
                })
                .orElse(EUR);
    }
}
