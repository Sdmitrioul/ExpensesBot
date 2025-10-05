package com.dskroba.expenses;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

public final class ExpenseUtils {
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");

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
            return Optional.of(ExpenseTag.valueOf(tag));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    private ExpenseUtils() {
    }
}
