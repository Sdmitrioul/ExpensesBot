package com.dskroba.base.formatter;

import com.dskroba.type.ContentType;

import java.util.List;

public interface Formatter {
    ContentType contentType();

    <T> byte[] formatOutput(List<FieldSelector<T>> selectors, List<T> values);
}
