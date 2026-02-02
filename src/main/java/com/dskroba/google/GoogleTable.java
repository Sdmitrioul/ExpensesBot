package com.dskroba.google;

public record GoogleTable(
        String spreadsheetId,
        String sheetName
) {
    public String range() {
        return sheetName + "!A:F";
    }
}
