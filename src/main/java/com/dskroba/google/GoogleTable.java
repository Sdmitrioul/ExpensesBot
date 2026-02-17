package com.dskroba.google;

import com.dskroba.base.exception.CustomException;

import java.util.List;
import java.util.Optional;

public record GoogleTable(
        String documentId,
        String sheetName
) {
    public String range() {
        return sheetName + "!A:F";
    }

    public static List<Object> header() {
        return List.of("Tag", "Price", "Currency", "Description", "Created by", "Time");
    }

    public static GoogleTable fromLink(String link, String sheetName) {
        return new GoogleTable(documentIdFromLink(link).orElseThrow(() -> new CustomException("Unsupported link")), sheetName);
    }

    public static Optional<String> documentIdFromLink(String link) {
        int start = link.indexOf("/d/");
        if (start == -1) {
            return Optional.empty();
        }
        start += 3;
        int end = link.indexOf("/", start);
        return Optional.of(end == -1 ? link.substring(start) : link.substring(start, end));
    }
}
