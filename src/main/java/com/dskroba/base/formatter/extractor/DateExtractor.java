package com.dskroba.base.formatter.extractor;

import com.dskroba.base.Utils;
import com.dskroba.base.formatter.extractor.type.DateValue;

import java.util.Date;
import java.util.function.Function;

public non-sealed interface DateExtractor<T> extends Extractor<T, DateValue> {
    @Override
    default String extractString(T value) {
        return Utils.datePrinter(extractDate(value));
    }

    default Date extractDate(T value) {
        return extract(value).value();
    }

    static <T> DateExtractor<T> of(Function<T, Date> function) {
        return function.andThen(DateValue::new)::apply;
    }
}
