package com.dskroba.type;

import com.dskroba.base.Utils;

import java.time.Instant;
import java.util.Date;

public record Timeframe(Instant from, Instant to) {
    public String toPrettyString() {
        return Utils.datePrinter(Date.from(from)) + " - " + Utils.datePrinter(Date.from(to));
    }
}
