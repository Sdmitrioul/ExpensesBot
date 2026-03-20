package com.dskroba.base.formatter.extractor;

import com.dskroba.base.formatter.extractor.type.BooleanValue;

import java.util.function.Function;

public non-sealed interface BooleanExtractor<T> extends Extractor<T, BooleanValue> {
    @Override
    default String extractString(T value) {
        return String.valueOf(extract(value));
    }

    default Boolean extractBoolean(T value) {
        return extract(value).value();
    }

    static <T> BooleanExtractor<T> of(Function<T, Boolean> function) {
        return function.andThen(BooleanValue::new)::apply;
    }
}
