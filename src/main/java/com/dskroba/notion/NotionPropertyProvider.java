package com.dskroba.notion;

public class NotionPropertyProvider {
    private final String notionUrl;
    private final String token;
    private final String version;
    private final String databaseId;

    public NotionPropertyProvider(String notionUrl, String token, String version, String databaseId) {
        this.notionUrl = notionUrl;
        this.token = token;
        this.version = version;
        this.databaseId = databaseId;
    }

    public String getNotionUrl() {
        return notionUrl;
    }

    public String getToken() {
        return token;
    }

    public String getVersion() {
        return version;
    }

    public String getDatabaseId() {
        return databaseId;
    }

    public int getRetryCount() {
        return 0;
    }

    public long getRetryDelay() {
        return 0;
    }
}
