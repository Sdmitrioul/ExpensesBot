package com.dskroba.notion;

import com.dskroba.base.RateLimiter;
import com.dskroba.base.http.AbstractClient;

import java.io.Reader;
import java.net.http.HttpRequest;
import java.util.List;

public class NotionClient extends AbstractClient {
    private final NotionPropertyProvider propertyProvider;
    private final NotionHeadersProvider headersProvider;

    public NotionClient(NotionPropertyProvider propertyProvider, RateLimiter rateLimiter) {
        super(rateLimiter);
        this.propertyProvider = propertyProvider;
        this.headersProvider = new NotionHeadersProvider(propertyProvider.getToken(), propertyProvider.getVersion());
    }

    public List<DatabaseProperties> getDatabaseProperties() {
        return loadWebResource(
                buildUri(propertyProvider.getNotionUrl(), "/databases/" + propertyProvider.getDatabaseId()),
                headersProvider.getGetHeader(),
                "GET",
                HttpRequest.BodyPublishers.noBody(),
                this::processProperties,
                propertyProvider.getRetryCount(),
                propertyProvider.getRetryDelay()
        );
    }

    private List<DatabaseProperties> processProperties(Reader reader) {
        return null;
    }
}
