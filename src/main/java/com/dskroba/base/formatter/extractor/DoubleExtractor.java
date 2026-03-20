package com.dskroba.base.formatter.extractor;

import com.dskroba.base.Utils;
import com.dskroba.base.formatter.extractor.type.DoubleValue;

import java.util.function.Function;

public non-sealed interface DoubleExtractor<T> extends Extractor<T, DoubleValue> {
    @Override
    default String extractString(T value) {
        return Utils.doublePrinter(extractDouble(value));
    }

    default Double extractDouble(T value) {
        return extract(value).value();
    }

    static <T> DoubleExtractor<T> of(Function<T, Double> function) {
        return function.andThen(DoubleValue::new)::apply;
    }
}
