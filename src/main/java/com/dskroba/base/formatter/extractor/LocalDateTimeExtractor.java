package com.dskroba.base.formatter.extractor;

import com.dskroba.base.Utils;
import com.dskroba.base.formatter.extractor.type.DateTimeValue;

import java.time.LocalDateTime;
import java.util.function.Function;

public non-sealed interface LocalDateTimeExtractor<T> extends Extractor<T, DateTimeValue> {
    @Override
    default String extractString(T value) {
        return Utils.dateTimePrinter(extractLocalDateTime(value));
    }

    default LocalDateTime extractLocalDateTime(T value) {
        return extract(value).value();
    }

    static <T> LocalDateTimeExtractor<T> of(Function<T, LocalDateTime> function) {
        return function.andThen(DateTimeValue::new)::apply;
    }
}
