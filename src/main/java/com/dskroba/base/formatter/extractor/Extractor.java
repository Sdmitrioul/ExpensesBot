package com.dskroba.base.formatter.extractor;

import com.dskroba.base.formatter.extractor.type.TypeValue;

public sealed interface Extractor<T, R extends TypeValue<?>> permits
        StringExtractor,
        IntegerExtractor,
        LongExtractor,
        DoubleExtractor,
        BooleanExtractor,
        DateExtractor,
        LocalDateTimeExtractor {

    R extract(T value);

    String extractString(T value);
}
