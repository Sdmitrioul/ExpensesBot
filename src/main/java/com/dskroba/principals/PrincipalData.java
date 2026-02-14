package com.dskroba.principals;

import com.dskroba.google.GoogleTable;
import com.dskroba.type.Currency;

import java.util.List;
import java.util.Objects;

public record PrincipalData(
        List<GoogleTable> tables,
        Currency activeCurrency,
        String activeTableId
) {
    public GoogleTable activeTable() {
        for (var table : tables) {
            if (Objects.equals(table.documentId(), activeTableId)) {
                return table;
            }
        }
        return null;
    }
}