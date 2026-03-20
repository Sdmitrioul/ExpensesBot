package com.dskroba.base.csv;

public record RowEntry<T>(long rowNumber, T value) {
}
