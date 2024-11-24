package com.dskroba.type;

import com.dskroba.base.type.Pair;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.function.Function;

public enum DateFilter {
    TODAY(clock -> {
        LocalDateTime now = LocalDateTime.now(clock);
        ZoneId zone = clock.getZone();
        LocalDate today = now.toLocalDate();
        return new Pair<>(
                today.atStartOfDay(zone).toInstant(),
                today.plusDays(1).atStartOfDay(zone).toInstant());
    }),
    WEEK(clock -> {
        LocalDateTime now = LocalDateTime.now(clock);
        ZoneId zone = clock.getZone();
        LocalDate startOfWeek = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).toLocalDate();
        LocalDate startOfNextWeek = startOfWeek.plusWeeks(1);
        return new Pair<>(
                startOfWeek.atStartOfDay(zone).toInstant(),
                startOfNextWeek.atStartOfDay(zone).toInstant());
    }),
    MONTH(clock -> {
        LocalDateTime now = LocalDateTime.now(clock);
        ZoneId zone = clock.getZone();
        LocalDate startOfMonth = now.with(TemporalAdjusters.firstDayOfMonth()).toLocalDate();
        LocalDate startOfNextMonth = startOfMonth.plusMonths(1);
        return new Pair<>(
                startOfMonth.atStartOfDay(zone).toInstant(),
                startOfNextMonth.atStartOfDay(zone).toInstant());
    });

    private final Function<Clock, Pair<Instant, Instant>> transformer;

    DateFilter(Function<Clock, Pair<Instant, Instant>> transformer) {
        this.transformer = transformer;
    }

    public Pair<Instant, Instant> getTimeIntervals(Clock clock) {
        return transformer.apply(clock);
    }
}
