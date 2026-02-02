package com.dskroba.principals;

import com.dskroba.google.GoogleTable;
import com.dskroba.type.Currency;

import java.util.List;
import java.util.Objects;

public record PrincipalContext(
        List<GoogleTable> tables,
        Currency activeCurrency,
        String activeTableId
) {
    public GoogleTable activeTable() {
        for (var table : tables) {
            if (Objects.equals(table.spreadsheetId(), activeTableId)) {
                return table;
            }
        }
        return null;
    }
}