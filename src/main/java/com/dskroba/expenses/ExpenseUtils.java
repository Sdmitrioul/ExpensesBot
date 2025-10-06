package com.dskroba.expenses;

import java.text.ParseException;
import java.util.Date;
import java.util.Optional;

import static com.dskroba.base.Utils.SIMPLE_DATE_FORMAT;

public final class ExpenseUtils {
    public static Optional<Date> parseDate(String message) {
        try {
            return Optional.of(SIMPLE_DATE_FORMAT.parse(message));
        } catch (ParseException e) {
            return Optional.empty();
        }
    }

    public static Optional<Double> parseAmount(String amount) {
        if (amount == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(Double.parseDouble(amount));
        } catch (IllegalArgumentException ignored) {
            return Optional.empty();
        }
    }

    public static Optional<ExpenseTag> parseTag(String tag) {
        try {
            return Optional.of(ExpenseTag.valueOf(tag.toUpperCase()));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    private ExpenseUtils() {
    }
}
