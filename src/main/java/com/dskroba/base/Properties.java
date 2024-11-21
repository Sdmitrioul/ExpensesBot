package com.dskroba.base;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Properties {
    private static final Logger LOGGER = LogManager.getLogger(Properties.class);

    private final Map<String, Object> properties;

    public Properties(Map<String, Object> properties) {
        validateProperties(properties);
        this.properties = properties;
    }

    private void validateProperties(Map<String, Object> properties) {
        if (properties == null) {
            throw new IllegalArgumentException("Properties cannot be null");
        }

        boolean containsAllRequiredProps = Arrays.stream(Property.values())
                .filter(property -> property.required)
                .map(property -> property.name)
                .map(propertyName -> {
                    boolean containsed = properties.containsKey(propertyName);
                    if (!containsed) {
                        LOGGER.error("Required property {} not found!", propertyName);
                    }
                    return containsed;
                })
                .reduce(true, Boolean::logicalAnd);
        if (!containsAllRequiredProps) {
            throw new IllegalArgumentException("Required properties are missing");
        }
    }

    public Object get(Property property) {
        return getOrDefault(property, null);
    }

    public Object get(String property) {
        return getOrDefault(property, null);
    }

    public Object getOrDefault(Property property, Object defaultValue) {
        return getOrDefault(property.name, defaultValue);
    }

    public Object getOrDefault(String property, Object defaultValue) {
        return properties.getOrDefault(property, defaultValue);
    }

    public enum Property {
        APPLICATION_NAME("application.name", true),
        TELEGRAM_BOT_TOKEN("telegram.bot.token", true),
        NOTION_API_TOKEN("notion.token", true),
        NOTION_DATABASE_ID("notion.database.id", true),
        NOTION_API_VERSION("notion.api.version", true),
        NOTION_DATABASE_URL("notion.database.url", true),;

        private static final Map<String, Property> NAME_TO_PROPERTY;

        static {
            NAME_TO_PROPERTY = new HashMap<>();
            Arrays.stream(Property.values()).forEach(property -> NAME_TO_PROPERTY.put(property.name, property));
        }

        public final String name;
        public final boolean required;

        Property(String name, boolean required) {
            this.name = name;
            this.required = required;
        }
    }
}
