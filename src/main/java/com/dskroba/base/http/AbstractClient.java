package com.dskroba.base.http;

import com.dskroba.base.bean.AbstractBean;
import com.dskroba.base.limiter.RateLimiter;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.function.Function;

public class AbstractClient extends AbstractBean implements Client {
    private static final Duration CONNECTION_TIMEOUT = Duration.ofSeconds(30);
    private static final Duration READ_TIMEOUT = Duration.ofSeconds(60);

    private final RateLimiter rateLimiter;
    private HttpClient client;

    private final Object retryLock = new Object();

    public AbstractClient(RateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    public <T> T loadWebResource(
            URI uri,
            String[] headers,
            String httpMethod,
            HttpRequest.BodyPublisher bodyPublisher,
            Function<Reader, T> contentProcessor,
            int retryCount,
            long retryDelay
    ) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .timeout(READ_TIMEOUT)
                .method(httpMethod, bodyPublisher)
                .headers(headers)
                .build();

        int tryCount = 0;
        while (isRunning() && ++tryCount <= retryCount) {
            if (tryCount > 1) {
                LOGGER.info("Retry #{}", tryCount);
            }

            try {
                rateLimiter.acquire();
                HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
                if (response.statusCode() >= 200 && response.statusCode() < 300) {
                    return contentProcessor.apply(new InputStreamReader(response.body(), StandardCharsets.UTF_8));
                } else {
                    LOGGER.warn("Unexpected response http status: {}, answer: {}",
                            response.statusCode(), new String(response.body().readAllBytes()));
                    return null;
                }
            } catch (InterruptedException e) {
                LOGGER.warn("HTTP request was interrupted. Can be ignored during application shutdown.");
            } catch (Exception e) {
                LOGGER.warn("Failed to request, will retry in {} ms", retryDelay, e);
                waitDelay(retryDelay);
            }
        }
        if (tryCount > retryCount) {
            throw new IllegalStateException("Too many tries to load corporate actions!");
        } else {
            return null;
        }
    }

    private void waitDelay(long retryDelay) {
        synchronized (retryLock) {
            long delayStart = System.currentTimeMillis();
            while (isRunning() && (System.currentTimeMillis() - delayStart) < retryDelay) {
                long remainingDelay = delayStart + retryDelay - System.currentTimeMillis();
                if (remainingDelay > 0) {
                    try {
                        retryLock.wait(remainingDelay);
                    } catch (InterruptedException ie) {
                        LOGGER.warn("Sleep before retry was interrupted. Can be ignored during application shutdown.");
                    }
                }
            }
        }
    }

    @Override
    public void startImpl() {
        this.client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .connectTimeout(CONNECTION_TIMEOUT)
                .build();
    }

    @Override
    public void stopImpl() {
        synchronized (retryLock) {
            client.close();
            retryLock.notifyAll();
        }
    }
}
