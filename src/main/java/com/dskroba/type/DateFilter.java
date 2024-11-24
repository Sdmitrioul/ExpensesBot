package com.dskroba.type;

import com.dskroba.base.type.Pair;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static java.time.temporal.ChronoUnit.*;

public enum DateFilter {
    TODAY(DAYS), WEEK(WEEKS), MONTH(MONTHS);
    private final ChronoUnit unit;

    DateFilter(ChronoUnit unit) {
        this.unit = unit;
    }

    public Pair<Instant, Instant> getTimeIntervals(Clock clock) {
        Instant from = clock.instant().truncatedTo(unit);
        return new Pair<>(from, from.plus(1, unit));
    }
}
