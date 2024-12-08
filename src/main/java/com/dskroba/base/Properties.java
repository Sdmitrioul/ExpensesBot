package com.dskroba.base;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Properties {
    private final Map<String, String> properties;

    public Properties(Map<String, String> properties) {
        validateProperties(properties);
        this.properties = properties;
    }

    private void validateProperties(Map<String, String> properties) {
        if (properties == null) {
            throw new IllegalArgumentException("Properties cannot be null");
        }
        /*boolean containsAllRequiredProps = Arrays.stream(Property.values())
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
        }*/
    }

    public String get(Property property) {
        return getOrDefault(property, null);
    }

    public String get(String property) {
        return getOrDefault(property, null);
    }

    public String getOrDefault(Property property, String defaultValue) {
        return getOrDefault(property.name, defaultValue);
    }

    public String getOrDefault(String property, String defaultValue) {
        return properties.getOrDefault(property, defaultValue);
    }

    public enum Property {
        APPLICATION_NAME("application.name", true),
        APPLICATION_TIME_ZONE("application.timezone", true),
        TELEGRAM_BOT_TOKEN("telegram.bot.token", true),
        TELEGRAM_ALLOWED_USERS("telegram.allowed.users", true),
        NOTION_API_TOKEN("notion.token", true),
        NOTION_DATABASE_ID("notion.database.id", true),
        NOTION_API_VERSION("notion.api.version", true),
        NOTION_DATABASE_URL("notion.database.url", true),
        NOTION_RATE_LIMIT_DURATION("notion.rate.limit.duration", true),
        NOTION_RATE_LIMIT_THRESHOLD("notion.rate.limit.threshold", true),
        NOTION_RATE_LIMIT_RETRY("notion.rate.limit.retry", true);

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
