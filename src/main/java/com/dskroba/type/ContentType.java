package com.dskroba.type;

public record ContentType(String value) {
    public static final ContentType CSV = new ContentType("text/csv");
}
