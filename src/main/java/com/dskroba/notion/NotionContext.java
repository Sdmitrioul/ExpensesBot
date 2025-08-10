package com.dskroba.notion;

import com.dskroba.base.http.AbstractClient;
import com.dskroba.base.http.Client;
import com.dskroba.base.limiter.RateLimiter;
import com.dskroba.base.limiter.RateLimiterImpl;
import com.dskroba.configurations.properties.NotionProperties;

import java.time.Clock;

public final class NotionContext implements AutoCloseable {
    private final Clock clock;
    private final NotionProperties properties;
    private NotionFacade facade;
    private RateLimiter rateLimiter;
    private Client client;

    public NotionContext(Clock clock, NotionProperties properties) {
        this.clock = clock;
        this.properties = properties;
    }

    public synchronized NotionFacade getFacade() {
        if (facade != null) {
            return facade;
        }

        NotionFacade facade = new NotionFacadeImpl(
                getClient(),
                properties,
                new NotionHeadersProvider(properties.token(), properties.api().version()));
        this.facade = facade;
        return facade;
    }

    private Client getClient() {
        if (client == null) {
            AbstractClient client = new AbstractClient(getRateLimiter());
            client.start();
            this.client = client;
        }
        return this.client;
    }

    private synchronized RateLimiter getRateLimiter() {
        if (rateLimiter != null) {
            return rateLimiter;
        }
        this.rateLimiter = new RateLimiterImpl(
                clock,
                properties.rateLimit().duration(),
                properties.rateLimit().threshold(),
                properties.rateLimit().retry());
        return rateLimiter;
    }

    @Override
    public void close() throws Exception {
        if (client != null) {
            client.close();
        }
    }
}
