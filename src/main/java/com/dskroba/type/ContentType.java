package com.dskroba.type;

public record ContentType(String value, String filenameExtension) {
    public static final ContentType CSV = new ContentType("text/csv", ".csv");
    public static final ContentType XLSX = new ContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", ".xlsx");
    public static final ContentType PNG = new ContentType("image/png", ".png");
}
