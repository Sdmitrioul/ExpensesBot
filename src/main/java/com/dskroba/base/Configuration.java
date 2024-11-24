package com.dskroba.base;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public final class Configuration {
    private static final Logger LOGGER = LogManager.getLogger(Configuration.class);

    private static final String DEFAULT_CONFIG_FILE_PROP = "instance.conf";
    private static volatile Properties globalProperties;

    public static Properties getGlobalProperties() {
        if (globalProperties == null) {
            synchronized (Configuration.class) {
                if (globalProperties == null) {
                    globalProperties = readProperties();
                }
            }
        }
        return globalProperties;
    }

    private static Properties readProperties() {
        String path = System.getProperty(DEFAULT_CONFIG_FILE_PROP);
        LOGGER.info("Read properties from files (-D" + DEFAULT_CONFIG_FILE_PROP + "={}).", path);
        if (path == null) {
            LOGGER.error("Can't read config file.");
            throw new IllegalArgumentException("Can't read config file. " +
                    "Set '-D" + DEFAULT_CONFIG_FILE_PROP + "' to correct application properties file.");
        }
        return getPropertiesFromPaths(path);
    }


    private static Properties getPropertiesFromPaths(String allPaths) {
        String[] paths = allPaths.split(";");

        Map<String, String> propertyMap = new HashMap<>();
        for (String path : paths) {
            try (BufferedReader reader = Files.newBufferedReader(Paths.get(path))) {
                parseLines(reader, propertyMap, path);
            } catch (FileNotFoundException e) {
                LOGGER.error("Can't read properties from non-existing config file: {}", path);
                throw new IllegalArgumentException("Can't read properties from non-existing config file: " + path, e);
            } catch (IOException e) {
                LOGGER.error("Can't read properties from file: {}, {}", path, e);
                throw new RuntimeException("Can't read properties from file: " + path, e);
            }
        }

        return new Properties(propertyMap);
    }

    private static void parseLines(BufferedReader reader,
                                   Map<String, String> propertyMap,
                                   String path) throws IOException {
        String line;
        int lineNumber = 0;
        while ((line = reader.readLine()) != null) {
            lineNumber++;
            String[] parts = line.split("=", 2);

            if (parts.length != 2) {
                LOGGER.warn("File: {}; Can't parse variable at line {}, value {}", path, lineNumber, line);
                continue;
            }

            String propertyName = parts[0].trim();
            String propertyValue = parts[1].trim();

            propertyMap.put(propertyName, propertyValue);
        }
    }

    private Configuration() {
    }
}
