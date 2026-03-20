package com.dskroba.base.formatter.extractor;

import com.dskroba.base.formatter.extractor.type.LongValue;

import java.util.function.Function;

public non-sealed interface LongExtractor<T> extends Extractor<T, LongValue> {
    @Override
    default String extractString(T value) {
        return String.valueOf(extractLong(value));
    }

    default Long extractLong(T value) {
        return extract(value).value();
    }

    static <T> LongExtractor<T> of(Function<T, Long> function) {
        return value -> function.andThen(LongValue::new).apply(value);
    }
}
