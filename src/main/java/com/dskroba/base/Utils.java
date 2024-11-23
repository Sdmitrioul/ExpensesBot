package com.dskroba.base;

import com.dskroba.base.exception.CustomException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.net.URISyntaxException;

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

    private Utils() {
    }
}
