package com.dskroba.base;

import com.dskroba.base.exception.CustomException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public final class Utils {
    private static final Logger LOGGER = LogManager.getLogger(Utils.class);

    public static URI buildUri(String basePath, String additional) {
        try {
            return new URI(basePath).resolve(additional);
        } catch (URISyntaxException e) {
            LOGGER.error("Failed to build URI from path: {}", basePath, e);
            throw new CustomException(e);
        }
    }

    public static void shutdownExecutorService(ExecutorService service) {
        service.shutdown();
        try {
            if (!service.awaitTermination(60, TimeUnit.SECONDS)) {
                service.shutdownNow();
                if (!service.awaitTermination(60, TimeUnit.SECONDS))
                    System.err.println("Pool did not terminate");
            }
        } catch (InterruptedException ie) {
            service.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private Utils() {
    }
}
