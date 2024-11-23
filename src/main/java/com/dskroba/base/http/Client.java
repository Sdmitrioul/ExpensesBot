package com.dskroba.base.http;

import java.io.Reader;
import java.net.URI;
import java.net.http.HttpRequest;
import java.util.function.Function;

public interface Client {
    <T> T loadWebResource(
            URI uri,
            String[] headers,
            String httpMethod,
            HttpRequest.BodyPublisher bodyPublisher,
            Function<Reader, T> contentProcessor,
            int retryCount,
            long retryDelay
    );
}
