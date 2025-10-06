package com.dskroba.type;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.function.Function;

public enum DateFilter {
    TODAY(clock -> {
        LocalDate today = LocalDate.now(clock);
        return createDayTimeframe(today, clock.getZone());
    }),
    WEEK(clock -> {
        LocalDate startOfWeek = LocalDate.now(clock)
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = startOfWeek.plusWeeks(1);
        return createTimeframe(startOfWeek, endOfWeek, clock.getZone());
    }),
    MONTH(clock -> {
        LocalDate startOfMonth = LocalDate.now(clock)
                .with(TemporalAdjusters.firstDayOfMonth());
        LocalDate startOfNextMonth = startOfMonth.plusMonths(1);
        return createTimeframe(startOfMonth, startOfNextMonth, clock.getZone());
    }),
    PREVIOUS_MONTH(clock -> {
        LocalDate startOfPrevMonth = LocalDate.now(clock)
                .with(TemporalAdjusters.firstDayOfMonth())
                .minusMonths(1);
        LocalDate startOfCurrentMonth = startOfPrevMonth.plusMonths(1);
        return createTimeframe(startOfPrevMonth, startOfCurrentMonth, clock.getZone());
    }),
    ALL_HISTORY(_ -> new Timeframe(Instant.MIN, Instant.MAX));

    private final Function<Clock, Timeframe> transformer;

    DateFilter(Function<Clock, Timeframe> transformer) {
        this.transformer = transformer;
    }

    public Timeframe getTimeframe(Clock clock) {
        return transformer.apply(clock);
    }

    private static Timeframe createDayTimeframe(LocalDate date, ZoneId zone) {
        return createTimeframe(date, date.plusDays(1), zone);
    }

    private static Timeframe createTimeframe(LocalDate start, LocalDate exclusiveEnd, ZoneId zone) {
        return new Timeframe(
                start.atStartOfDay(zone).toInstant(),
                exclusiveEnd.atStartOfDay(zone).toInstant());
    }
}
