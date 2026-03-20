package com.dskroba.base.formatter.extractor;

import com.dskroba.base.formatter.extractor.type.TypeValue;

public sealed interface Extractor<T, R extends TypeValue<?>> permits BooleanExtractor, DateExtractor, DoubleExtractor, EnumExtractor, IntegerExtractor, LocalDateTimeExtractor, LongExtractor, StringExtractor {

    R extract(T value);

    String extractString(T value);
}
