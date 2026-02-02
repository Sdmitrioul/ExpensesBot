package com.dskroba.storage;

public interface ValueConverter<V> {
    V parse(String value);

    String convertToString(V value);
}
