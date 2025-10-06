package com.dskroba.base.formatter.extractor;


import com.dskroba.base.formatter.extractor.type.StringValue;

import java.util.function.Function;

public non-sealed interface EnumExtractor<T> extends Extractor<T, StringValue> {
    @Override
    default String extractString(T value) {
        return extract(value).value();
    }

    static <T> EnumExtractor<T> of(Function<T, Enum<?>> function) {
        return function.andThen(Enum::name).andThen(StringValue::new)::apply;
    }
}