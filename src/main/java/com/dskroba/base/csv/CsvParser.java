package com.dskroba.base.csv;

import java.io.InputStream;
import java.util.function.Consumer;

public interface CsvParser<T> {
    String parse(InputStream inputStream,
               Consumer<RowEntry<T>> entryConsumer,
               Consumer<IncorrectRowEntry> unparsedDataConsumer);
}
