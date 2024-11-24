package com.dskroba.notion;

import com.dskroba.base.Configuration;
import com.dskroba.base.Properties;
import com.dskroba.base.http.AbstractClient;
import com.dskroba.base.http.Client;
import com.dskroba.base.limiter.RateLimiter;
import com.dskroba.base.limiter.RateLimiterImpl;

import java.time.Clock;
import java.time.Duration;

import static com.dskroba.base.Properties.Property.*;

public final class NotionContext {
    private final Clock clock;
    private final NotionPropertyProvider propertyProvider;
    private NotionFacade facade;
    private RateLimiter rateLimiter;

    public NotionContext(Clock clock) {
        this.clock = clock;
        this.propertyProvider = buildPropertyProvider();
    }

    private static NotionPropertyProvider buildPropertyProvider() {
        Properties properties = Configuration.getGlobalProperties();
        return new NotionPropertyProvider(
                properties.get(NOTION_DATABASE_URL),
                properties.get(NOTION_API_TOKEN),
                properties.get(NOTION_API_VERSION),
                properties.get(NOTION_DATABASE_ID)
        );
    }

    public synchronized NotionFacade getFacade() {
        if (facade != null) {
            return facade;
        }

        NotionFacade facade = new NotionFacadeImpl(
                getClient(),
                propertyProvider,
                new NotionHeadersProvider(propertyProvider.getToken(), propertyProvider.getVersion()));
        this.facade = facade;
        return facade;
    }

    private Client getClient() {
        return new AbstractClient(getRateLimiter());
    }

    private synchronized RateLimiter getRateLimiter() {
        if (rateLimiter != null) {
            return rateLimiter;
        }
        Properties properties = Configuration.getGlobalProperties();
        this.rateLimiter = new RateLimiterImpl(
                clock,
                Duration.parse(properties.get(NOTION_RATE_LIMIT_DURATION)),
                Integer.parseInt(properties.get(NOTION_RATE_LIMIT_THRESHOLD)),
                Integer.parseInt(properties.get(NOTION_RATE_LIMIT_RETRY)));
        return rateLimiter;
    }
}
