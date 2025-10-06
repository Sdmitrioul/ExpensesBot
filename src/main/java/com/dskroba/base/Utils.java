package com.dskroba.base;

import com.dskroba.base.exception.CustomException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public final class Utils {
    private static final Logger LOGGER = LogManager.getLogger(Utils.class);

    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss a");

    public static URI buildUri(String basePath, String additional) {
        try {
            return new URI(basePath).resolve(additional);
        } catch (URISyntaxException e) {
            LOGGER.error("Failed to build URI from path: {}", basePath, e);
            throw new CustomException(e);
        }
    }

    public static String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1);
    }

    public static String doublePrinter(Double value) {
        return String.format("%.2f", value);
    }

    public static String datePrinter(Date date) {
        return SIMPLE_DATE_FORMAT.format(date);
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

    public static String dateTimePrinter(LocalDateTime time) {
        return DATE_TIME_FORMATTER.format(time);
    }
}
