package com.dskroba.base;

import java.time.Clock;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

public final class ClockProvider {
    private static final Clock INSTANCE_UTC = Clock.systemUTC();
    private static Clock INSTANCE = null;

    public static Clock getClock() {
        return Optional.ofNullable(INSTANCE).orElse(INSTANCE_UTC);
    }

    public static Date getCurrentDate() {
        return Date.from(getClock().instant());
    }

    public static ZoneId getZoneId() {
        return getClock().getZone();
    }

    public synchronized static void setClock(Clock clock) {
        INSTANCE = clock;
    }
}
