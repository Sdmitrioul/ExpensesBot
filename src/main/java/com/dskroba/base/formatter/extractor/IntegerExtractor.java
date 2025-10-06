package com.dskroba.base.formatter.extractor;

import com.dskroba.base.formatter.extractor.type.IntegerValue;

import java.util.function.Function;

public non-sealed interface IntegerExtractor<T> extends Extractor<T, IntegerValue> {
    @Override
    default String extractString(T value) {
        return String.valueOf(extractInteger(value));
    }

    default Integer extractInteger(T value) {
        return extract(value).value();
    }

    static <T> IntegerExtractor<T> of(Function<T, Integer> function) {
        return function.andThen(IntegerValue::new)::apply;
    }
}
