package com.dskroba.base.formatter.extractor.type;

public sealed interface TypeValue<T> permits BooleanValue, IntegerValue, LongValue, DoubleValue, StringValue, DateValue, DateTimeValue {
    T value();
}
