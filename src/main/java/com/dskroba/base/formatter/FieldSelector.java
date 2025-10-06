package com.dskroba.base.formatter;

import com.dskroba.base.formatter.extractor.Extractor;
import com.dskroba.base.formatter.extractor.type.TypeValue;

public interface FieldSelector<T> {
    String name();

    Extractor<T, ? extends TypeValue<?>> valueExtractor();
}
