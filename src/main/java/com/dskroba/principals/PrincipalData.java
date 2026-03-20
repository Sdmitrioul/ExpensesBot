package com.dskroba.principals;

import com.dskroba.google.GoogleTable;
import com.dskroba.type.Currency;

import java.util.List;

public record PrincipalData(
        List<GoogleTable> tables,
        Currency activeCurrency,
        GoogleTable activeTable
) {
}