package com.dskroba.base.csv;

public record IncorrectRowEntry(long rowNumber, String causeMessage, String originalEntry) {
}
