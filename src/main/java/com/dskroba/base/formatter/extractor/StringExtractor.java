package com.dskroba.base.formatter.extractor;

import com.dskroba.base.formatter.extractor.type.StringValue;

import java.util.function.Function;

public non-sealed interface StringExtractor<T> extends Extractor<T, StringValue> {
    @Override
    default String extractString(T value) {
        return "\"" + extract(value).value() + "\"";
    }

    static <T> StringExtractor<T> of(Function<T, String> function) {
        return function.andThen(StringValue::new)::apply;
    }
}
